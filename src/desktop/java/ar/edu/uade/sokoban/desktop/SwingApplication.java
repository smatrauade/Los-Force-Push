package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Native Swing entry point. All UI construction and updates happen on the EDT. */
public final class SwingApplication {
    private SwingApplication() { }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso: SwingApplication [nivel.txt]");
            System.exit(2);
        }
        Path initial = null;
        try {
            if (args.length == 1) {
                initial = Path.of(args[0]);
            }
        } catch (InvalidPathException exception) {
            System.err.println("Ruta de nivel invalida: " + exception.getReason());
            System.exit(2);
        }
        Path initialLevel = initial;
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
            if (initialLevel != null) {
                window.controller.load(initialLevel);
            } else {
                window.chooseLevel();
            }
        });
    }

    private static final class GameWindow extends JFrame implements GameView {
        private static final long serialVersionUID = 1L;
        private final BoardPanel board = new BoardPanel();
        private final JLabel level = new JLabel("Sin nivel cargado");
        private final JLabel counters = new JLabel("Movimientos: 0   |   Empujes: 0");
        private final transient GameController controller = new GameController(
                new LevelLoader(new LevelParser()), new MovementEngine(), this);

        private GameWindow() {
            super("Los Force Push | Sokoban - Swing experimental");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JPanel heading = new JPanel();
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            heading.setBorder(BorderFactory.createEmptyBorder(14, 18, 12, 18));
            JLabel title = new JLabel("LOS FORCE PUSH  /  SOKOBAN");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
            heading.add(title);
            heading.add(new JLabel("Spike jugable · reglas y gráficos experimentales"));
            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING, 12, 4));
            JButton open = new JButton("Abrir TXT...");
            open.addActionListener(event -> chooseLevel());
            toolbar.add(open);
            toolbar.add(level);
            toolbar.add(counters);
            heading.add(toolbar);
            add(heading, BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(board);
            scroll.getVerticalScrollBar().setFocusable(false);
            scroll.getHorizontalScrollBar().setFocusable(false);
            scroll.setFocusable(false);
            add(scroll, BorderLayout.CENTER);
            JLabel help = new JLabel(
                    "Flechas: mover / empujar   ·   Círculo verde: destino   ·   Sin Undo, victoria ni audio");
            help.setBorder(BorderFactory.createEmptyBorder(10, 18, 12, 18));
            add(help, BorderLayout.SOUTH);
            ArrowBindings.install(getRootPane(), controller::move);
            setMinimumSize(new Dimension(760, 480));
            setSize(920, 640);
            setLocationByPlatform(true);
        }

        private void chooseLevel() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Abrir nivel TXT experimental");
            chooser.setFileFilter(new FileNameExtensionFilter("Nivel TXT", "txt"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.load(chooser.getSelectedFile().toPath());
            }
        }

        @Override
        public void render(GamePresentation presentation) {
            level.setText("Nivel: " + presentation.levelName());
            counters.setText("Movimientos: " + presentation.moves()
                    + "   |   Empujes: " + presentation.pushes());
            board.showBoard(presentation.board());
        }

        @Override
        public void showLoadError(String message) {
            JOptionPane.showMessageDialog(this, message, "Error de carga", JOptionPane.ERROR_MESSAGE);
        }
    }
}
