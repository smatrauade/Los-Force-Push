package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Desktop pixel UI. Model and rules remain in the existing core/controller. */
public final class SwingApplication {
    private SwingApplication() { }

    public static void main(String[] args) {
        if (args.length > 2 || (args.length == 2 && !args[0].equals("--extended"))
                || (args.length == 1 && args[0].equals("--extended"))) {
            System.err.println("Uso: SwingApplication [nivel.txt | --extended nivel.txt]");
            System.exit(2);
        }
        Path requested = null;
        if (args.length > 0) {
            try {
                requested = Path.of(args[args.length - 1]);
            } catch (InvalidPathException exception) {
                System.err.println("Ruta de nivel invalida: " + exception.getReason());
                System.exit(2);
            }
        }
        Path initial = requested;
        boolean extended = args.length == 2;
        SwingUtilities.invokeLater(() -> {
            PixelTheme.install();
            GameWindow window = new GameWindow(initial);
            window.setVisible(true);
            if (initial != null) {
                window.openLevel(initial, extended ? LevelCatalog.Format.EXTENDED : LevelCatalog.Format.STANDARD);
            }
        });
    }

    private static final class GameWindow extends JFrame implements GameView {
        private static final long serialVersionUID = 1L;
        private static final String MENU = "MENU";
        private static final String GAME = "GAME";
        private static final String PAUSE = "PAUSE";
        private static final String OPTIONS = "OPTIONS";
        private static final String HELP = "HELP";
        private static final String FINISH = "FINISH";

        private final CardLayout layout = new CardLayout();
        private final JPanel cards = new JPanel(layout);
        private final BoardPanel board = new BoardPanel();
        private final transient SoundManager audio = new SoundManager();
        private final JLabel levelText = label("SIN NIVEL", PixelTheme.BODY);
        private final JLabel movesText = label("0000", PixelTheme.TITLE);
        private final JLabel pushesText = label("0000", PixelTheme.TITLE);
        private final JLabel status = label("LISTO PARA JUGAR", PixelTheme.SMALL);
        private final JLabel victoryData = label("", PixelTheme.BODY);
        private final JButton continueButton = PixelTheme.button("CONTINUAR");
        private final JButton restartButton = PixelTheme.button("R  REINICIAR");
        private final JButton pauseRestartButton = PixelTheme.button("REINICIAR NIVEL");
        private final JButton undoButton = PixelTheme.button("Z  DESHACER");
        private final JButton saveButton = PixelTheme.button("GUARDAR PARTIDA");
        private final JButton loadSaveButton = PixelTheme.button("CARGAR PARTIDA");
        private final JButton nextButton = PixelTheme.button("SIGUIENTE NIVEL");
        private final JCheckBox soundToggle = new JCheckBox("ACTIVAR EFECTOS DE SONIDO", true);
        private final JComboBox<String> zoomSelector = new JComboBox<>(new String[]{
                "1x  (32 px)", "2x  (64 px)", "3x  (96 px)"});
        private final transient GameController controller = new GameController(this);
        private transient Path currentLevel;
        private transient Path pendingLevel;
        private transient boolean activeSession;
        private String active = MENU;
        private String optionsReturn = MENU;
        private transient UiCommands commands = UiCommands.EMPTY;

        private GameWindow(Path initial) {
            super("LOS FORCE PUSH  |  SOKOBAN");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setMinimumSize(new Dimension(880, 720));
            setSize(1190, 790);
            setLocationRelativeTo(null);
            cards.setBackground(PixelTheme.BACKGROUND);
            cards.add(buildMenu(), MENU);
            cards.add(buildGame(), GAME);
            cards.add(buildPause(), PAUSE);
            cards.add(buildOptions(), OPTIONS);
            cards.add(buildHelp(), HELP);
            cards.add(buildFinish(), FINISH);
            add(cards);
            continueButton.setEnabled(false);
            restartButton.setEnabled(false);
            pauseRestartButton.setEnabled(false);
            pending(undoButton, "Undo requiere cinco movimientos y usos disponibles");
            pending(saveButton, "Guardar partida requiere la funcionalidad adicional");
            pending(loadSaveButton, "Cargar partida guardada requiere la funcionalidad adicional");
            pending(nextButton, "No hay siguiente nivel disponible");
            installKeys();
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    audio.close();
                }
            });
            showScreen(MENU);
        }

        private static JLabel label(String text, java.awt.Font font) {
            JLabel result = new JLabel(text);
            result.setFont(font);
            result.setForeground(PixelTheme.CREAM);
            return result;
        }

        private static void pending(JButton button, String explanation) {
            button.setEnabled(false);
            button.setToolTipText(explanation);
        }

        private void navigate(String name) {
            if (!name.equals(active)) {
                audio.play(SoundManager.Effect.MENU);
            }
            showScreen(name);
        }

        private void showScreen(String name) {
            active = name;
            layout.show(cards, name);
            getRootPane().requestFocusInWindow();
        }

        private JPanel centerStack(String heading, String subheading) {
            JPanel surface = PixelTheme.surface();
            surface.setLayout(new GridBagLayout());
            JPanel stack = new JPanel();
            stack.setOpaque(true);
            stack.setBackground(PixelTheme.SURFACE);
            stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
            PixelTheme.border(stack);
            stack.setPreferredSize(new Dimension(500, 520));
            JLabel h = label(heading, PixelTheme.TITLE);
            h.setForeground(PixelTheme.COPPER);
            h.setAlignmentX(Component.CENTER_ALIGNMENT);
            stack.add(Box.createVerticalStrut(15));
            stack.add(h);
            stack.add(Box.createVerticalStrut(12));
            JLabel sub = label(subheading, PixelTheme.SMALL);
            sub.setForeground(PixelTheme.MUTED);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            stack.add(sub);
            stack.add(Box.createVerticalStrut(23));
            surface.add(stack);
            // Card-specific buttons are inserted into the nested centered stack.
            surface.putClientProperty("stack", stack);
            return surface;
        }

        private static JPanel stackOf(JPanel card) {
            return (JPanel) Objects.requireNonNull(card.getClientProperty("stack"));
        }

        private static void addStackButton(JPanel stack, JButton button) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(395, 44));
            stack.add(button);
            stack.add(Box.createVerticalStrut(10));
        }

        private JPanel buildMenu() {
            JPanel card = centerStack("LOS FORCE PUSH", "SOKOBAN  /  LADRILLO Y COBRE");
            JPanel stack = stackOf(card);
            stack.setPreferredSize(new Dimension(500, 620));
            JPanel illustration = new JPanel() {
                private static final long serialVersionUID = 1L;
                private final transient PixelAssets art = new PixelAssets();
                @Override
                protected void paintComponent(java.awt.Graphics graphics) {
                    super.paintComponent(graphics);
                    java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
                    try {
                        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        int side = 68;
                        int start = (getWidth() - side * 3) / 2;
                        String[] icons = {"player_down", "box", "goal"};
                        for (int i = 0; i < icons.length; i++) {
                            g.drawImage(art.image("floor_" + i), start + i * side, 0,
                                    side, side, null);
                            g.drawImage(art.image(icons[i]), start + i * side, 0,
                                    side, side, null);
                        }
                    } finally {
                        g.dispose();
                    }
                }
            };
            illustration.setOpaque(false);
            illustration.setAlignmentX(Component.CENTER_ALIGNMENT);
            illustration.setPreferredSize(new Dimension(260, 68));
            illustration.setMaximumSize(new Dimension(350, 68));
            stack.add(illustration);
            stack.add(Box.createVerticalStrut(12));
            JButton newGame = PixelTheme.button("NUEVA PARTIDA  /  DEMO");
            newGame.addActionListener(event -> {
                audio.play(SoundManager.Effect.MENU);
                startDemo();
            });
            addStackButton(stack, newGame);
            continueButton.addActionListener(event -> navigate(GAME));
            addStackButton(stack, continueButton);
            JButton open = PixelTheme.button("ABRIR NIVEL TXT...");
            open.addActionListener(event -> {
                audio.play(SoundManager.Effect.MENU);
                chooseLevel();
            });
            addStackButton(stack, open);
            loadSaveButton.addActionListener(event -> runCommand(commands.loadSaved()));
            addStackButton(stack, loadSaveButton);
            JButton help = PixelTheme.button("COMO JUGAR");
            help.addActionListener(event -> navigate(HELP));
            addStackButton(stack, help);
            JButton options = PixelTheme.button("OPCIONES");
            options.addActionListener(event -> openOptions(MENU));
            addStackButton(stack, options);
            JButton exit = PixelTheme.button("SALIR");
            exit.addActionListener(event -> dispose());
            addStackButton(stack, exit);
            stack.add(Box.createVerticalGlue());
            return card;
        }

        private JPanel buildGame() {
            JPanel result = new JPanel(new BorderLayout());
            result.setBackground(PixelTheme.BACKGROUND);
            JPanel top = new JPanel(new BorderLayout(15, 0));
            top.setBackground(PixelTheme.SURFACE);
            top.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
            JLabel brand = label("LOS FORCE PUSH  /  SOKOBAN", PixelTheme.TITLE);
            brand.setForeground(PixelTheme.COPPER);
            top.add(brand, BorderLayout.WEST);
            JButton menu = PixelTheme.button("ESC  PAUSA");
            menu.setPreferredSize(new Dimension(160, 38));
            menu.addActionListener(event -> showScreen(PAUSE));
            top.add(menu, BorderLayout.EAST);
            result.add(top, BorderLayout.NORTH);

            JPanel boardCenter = PixelTheme.surface();
            boardCenter.setLayout(new GridBagLayout());
            boardCenter.add(board, new GridBagConstraints());
            JScrollPane viewport = new JScrollPane(boardCenter);
            viewport.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 8));
            viewport.setBackground(PixelTheme.BACKGROUND);
            viewport.getViewport().setBackground(PixelTheme.BACKGROUND);
            viewport.getVerticalScrollBar().setUnitIncrement(32);
            viewport.getHorizontalScrollBar().setUnitIncrement(32);
            viewport.setFocusable(false);
            result.add(viewport, BorderLayout.CENTER);

            JPanel hud = new JPanel();
            hud.setLayout(new BoxLayout(hud, BoxLayout.Y_AXIS));
            hud.setBackground(PixelTheme.SURFACE);
            PixelTheme.border(hud);
            hud.setPreferredSize(new Dimension(268, 0));
            hud.add(label("NIVEL ACTUAL", PixelTheme.SMALL));
            hud.add(Box.createVerticalStrut(7));
            levelText.setForeground(PixelTheme.COPPER);
            hud.add(levelText);
            hud.add(Box.createVerticalStrut(27));
            hud.add(label("MOVIMIENTOS", PixelTheme.SMALL));
            hud.add(movesText);
            hud.add(Box.createVerticalStrut(18));
            hud.add(label("EMPUJES", PixelTheme.SMALL));
            hud.add(pushesText);
            hud.add(Box.createVerticalStrut(25));
            undoButton.addActionListener(event -> runCommand(commands.undo()));
            addHudButton(hud, undoButton);
            restartButton.addActionListener(event -> runCommand(commands.restart()));
            addHudButton(hud, restartButton);
            JButton open = PixelTheme.button("ABRIR TXT...");
            open.addActionListener(event -> {
                audio.play(SoundManager.Effect.MENU);
                chooseLevel();
            });
            addHudButton(hud, open);
            saveButton.addActionListener(event -> runCommand(commands.save()));
            addHudButton(hud, saveButton);
            hud.add(Box.createVerticalStrut(12));
            status.setForeground(PixelTheme.AQUA);
            hud.add(status);
            result.add(hud, BorderLayout.EAST);
            JLabel footer = label(" FLECHAS MOVER     ESC PAUSA     Z UNDO*     R REINICIAR*     M SONIDO     +/- ZOOM",
                    PixelTheme.SMALL);
            footer.setForeground(PixelTheme.MUTED);
            footer.setBorder(BorderFactory.createEmptyBorder(7, 17, 12, 0));
            result.add(footer, BorderLayout.SOUTH);
            return result;
        }

        private static void addHudButton(JPanel stack, JButton button) {
            button.setMaximumSize(new Dimension(232, 45));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            stack.add(button);
            stack.add(Box.createVerticalStrut(10));
        }

        private JPanel buildPause() {
            JPanel card = centerStack("PAUSA", "PARTIDA EN CURSO");
            JPanel stack = stackOf(card);
            JButton resume = PixelTheme.button("CONTINUAR PARTIDA");
            resume.addActionListener(event -> navigate(GAME));
            addStackButton(stack, resume);
            pauseRestartButton.addActionListener(event -> runCommand(commands.restart()));
            addStackButton(stack, pauseRestartButton);
            JButton options = PixelTheme.button("OPCIONES");
            options.addActionListener(event -> openOptions(PAUSE));
            addStackButton(stack, options);
            JButton menu = PixelTheme.button("VOLVER AL MENU");
            menu.addActionListener(event -> navigate(MENU));
            addStackButton(stack, menu);
            stack.add(Box.createVerticalGlue());
            return card;
        }

        private JPanel buildOptions() {
            JPanel card = centerStack("OPCIONES", "GRAFICOS Y SONIDO");
            JPanel stack = stackOf(card);
            soundToggle.setFont(PixelTheme.BODY);
            soundToggle.setForeground(PixelTheme.CREAM);
            soundToggle.setBackground(PixelTheme.SURFACE);
            soundToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
            soundToggle.addActionListener(event -> audio.setEnabled(soundToggle.isSelected()));
            stack.add(soundToggle);
            stack.add(Box.createVerticalStrut(24));
            JLabel zoomLabel = label("TAMANO DE LOS SPRITES", PixelTheme.BODY);
            zoomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            stack.add(zoomLabel);
            stack.add(Box.createVerticalStrut(9));
            zoomSelector.setSelectedIndex(1);
            zoomSelector.setMaximumSize(new Dimension(320, 35));
            zoomSelector.setFont(PixelTheme.BODY);
            zoomSelector.addActionListener(event -> board.setScale(zoomSelector.getSelectedIndex() + 1));
            stack.add(zoomSelector);
            stack.add(Box.createVerticalStrut(28));
            JButton back = PixelTheme.button("VOLVER");
            back.addActionListener(event -> navigate(optionsReturn));
            addStackButton(stack, back);
            stack.add(Box.createVerticalGlue());
            return card;
        }

        private JPanel buildHelp() {
            JPanel card = centerStack("COMO JUGAR", "REGLAS BASICAS");
            JPanel stack = stackOf(card);
            for (String text : new String[]{
                    "FLECHAS: CAMINAR Y EMPUJAR",
                    "LAS CAJAS NO SE PUEDEN TIRAR",
                    "COLOCA CADA CAJA EN UN DESTINO",
                    "ESC: PAUSAR / CONTINUAR",
                    "R: REINICIAR  /  Z: DESHACER 5",
                    "M: ACTIVAR O DESACTIVAR AUDIO",
                    "ZOOM: + Y -",
                    "UNDO: HASTA 3 USOS POR NIVEL"}) {
                JLabel row = label(text, PixelTheme.SMALL);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                stack.add(row);
                stack.add(Box.createVerticalStrut(12));
            }
            JButton back = PixelTheme.button("VOLVER AL MENU");
            back.addActionListener(event -> navigate(MENU));
            addStackButton(stack, back);
            return card;
        }

        private JPanel buildFinish() {
            JPanel card = centerStack("NIVEL COMPLETADO", "RESUMEN FINAL");
            JPanel stack = stackOf(card);
            victoryData.setHorizontalAlignment(SwingConstants.CENTER);
            victoryData.setAlignmentX(Component.CENTER_ALIGNMENT);
            stack.add(victoryData);
            stack.add(Box.createVerticalStrut(20));
            nextButton.addActionListener(event -> runCommand(commands.nextLevel()));
            addStackButton(stack, nextButton);
            JButton menu = PixelTheme.button("VOLVER AL MENU");
            menu.addActionListener(event -> navigate(MENU));
            addStackButton(stack, menu);
            stack.add(Box.createVerticalGlue());
            return card;
        }

        private void openOptions(String origin) {
            optionsReturn = origin;
            navigate(OPTIONS);
        }

        private void startDemo() {
            Path root = Path.of(System.getProperty("forcepush.repo.root", "."), "src", "desktop", "resources", "levels");
            pendingLevel = root.resolve("campaign-a.txt");
            controller.loadCatalog(new LevelCatalog(List.of(
                    new LevelCatalog.Entry("nivel_1", root.resolve("campaign-a.txt"), LevelCatalog.Format.STANDARD),
                    new LevelCatalog.Entry("nivel_2", root.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED))));
            pendingLevel = null;
        }

        private void openLevel(Path path, LevelCatalog.Format format) {
            pendingLevel = Objects.requireNonNull(path, "path");
            controller.load(path, format);
            pendingLevel = null;
        }

        private void chooseLevel() {
            Object choice = JOptionPane.showInputDialog(this, "Formato del nivel", "Abrir TXT",
                    JOptionPane.QUESTION_MESSAGE, null, new String[]{"STANDARD", "EXTENDED"}, "STANDARD");
            if (choice == null) { return; }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Abrir nivel Sokoban (.txt)");
            chooser.setFileFilter(new FileNameExtensionFilter("Nivel TXT (*.txt)", "txt"));
            if (currentLevel != null && currentLevel.getParent() != null) { chooser.setCurrentDirectory(currentLevel.getParent().toFile()); }
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                openLevel(chooser.getSelectedFile().toPath(), LevelCatalog.Format.valueOf(choice.toString()));
            }
        }

        /** UI-only integration point. Callbacks are supplied by the other modules. */
        @Override
        public void configureUiCommands(UiCommands value) {
            commands = Objects.requireNonNull(value, "commands");
            undoButton.setEnabled(activeSession && value.undo() != null);
            restartButton.setEnabled(activeSession && value.restart() != null);
            pauseRestartButton.setEnabled(activeSession && value.restart() != null);
            saveButton.setEnabled(activeSession && value.save() != null);
            loadSaveButton.setEnabled(value.loadSaved() != null);
            nextButton.setEnabled(value.nextLevel() != null);
        }

        private static void runCommand(Runnable command) {
            if (command != null) {
                command.run();
            }
        }

        @Override
        public void playEffect(SoundManager.Effect effect) {
            audio.play(Objects.requireNonNull(effect, "effect"));
        }

        private void installKeys() {
            ArrowBindings.install(getRootPane(), direction -> {
                if (GAME.equals(active) && activeSession) {
                    board.setFacing(direction);
                    controller.move(direction);
                }
            });
            bind("ESCAPE", "pause", () -> {
                if (GAME.equals(active)) {
                    showScreen(PAUSE);
                } else if (PAUSE.equals(active)) {
                    showScreen(GAME);
                } else if (OPTIONS.equals(active)) {
                    showScreen(optionsReturn);
                } else if (!MENU.equals(active)) {
                    showScreen(MENU);
                }
            });
            bind("R", "restart", () -> {
                if (GAME.equals(active) && restartButton.isEnabled()) {
                    runCommand(commands.restart());
                }
            });
            bind("M", "mute", () -> {
                if (GAME.equals(active) || PAUSE.equals(active)) {
                    audio.setEnabled(!audio.isEnabled());
                    soundToggle.setSelected(audio.isEnabled());
                    status.setText(audio.isEnabled() ? "SONIDO ACTIVADO" : "SONIDO SILENCIADO");
                }
            });
            bind("Z", "undo-pending", () -> {
                if (GAME.equals(active)) {
                    if (undoButton.isEnabled()) {
                        runCommand(commands.undo());
                    } else {
                        status.setText("UNDO: REQUIERE 5 MOVIMIENTOS Y USOS DISPONIBLES");
                    }
                }
            });
            bind("PLUS", "zoom-in", () -> changeZoom(1));
            bind("EQUALS", "zoom-in-equals", () -> changeZoom(1));
            bind("MINUS", "zoom-out", () -> changeZoom(-1));
        }

        private void changeZoom(int delta) {
            if (GAME.equals(active)) {
                int value = Math.max(1, Math.min(3, board.getScale() + delta));
                zoomSelector.setSelectedIndex(value - 1);
                status.setText("ZOOM: " + value + "X");
            }
        }

        private void bind(String key, String id, Runnable callback) {
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(key), id);
            getRootPane().getActionMap().put(id, new AbstractAction() {
                private static final long serialVersionUID = 1L;
                @Override
                public void actionPerformed(ActionEvent event) {
                    callback.run();
                }
            });
        }

        @Override
        public void render(GamePresentation presentation) {
            if (pendingLevel != null) {
                currentLevel = pendingLevel;
                board.setFacing(Direction.DOWN);
            }
            String fullName = presentation.levelName().toUpperCase(java.util.Locale.ROOT);
            levelText.setText(fullName.length() > 18
                    ? fullName.substring(0, 15) + "..." : fullName);
            levelText.setToolTipText(fullName);
            movesText.setText(String.format(java.util.Locale.ROOT, "%04d", presentation.moves()));
            pushesText.setText(String.format(java.util.Locale.ROOT, "%04d", presentation.pushes()));
            board.showState(presentation.state());
            activeSession = true;
            continueButton.setEnabled(true);
            status.setText("UNDO: " + presentation.undoRemaining() + "  HISTORIAL: " + presentation.historyAvailable());
        }

        @Override
        public void onLevelStarted() { showScreen(GAME); }

        @Override
        public void onMove(MoveResult.Kind kind) {
            switch (kind) {
                case MOVED -> audio.play(SoundManager.Effect.STEP);
                case PUSHED -> audio.play(SoundManager.Effect.PUSH);
                case BLOCKED -> audio.play(SoundManager.Effect.BLOCKED);
            }
        }

        @Override
        public void showLoadError(String message) {
            JOptionPane.showMessageDialog(this, message, "Error de carga TXT",
                    JOptionPane.ERROR_MESSAGE);
        }

        @Override
        public void showVictory(LevelResult result) {
            Objects.requireNonNull(result, "result");
            victoryData.setText("<html><div style='text-align:center'>"
                    + "NIVEL: " + escapeHtml(result.levelName()) + "<br><br>"
                    + "MOVIMIENTOS: " + result.moves() + "<br>"
                    + "EMPUJES: " + result.pushes() + "<br>"
                    + "UNDO: " + result.undoUses() + "<br><br>"
                    + "PUNTAJE: " + result.score()
                    + (commands.nextLevel() == null ? "<br><br>CAMPANA COMPLETADA" : "") + "</div></html>");
            continueButton.setEnabled(false);
            audio.play(SoundManager.Effect.VICTORY);
            showScreen(FINISH);
        }

        private static String escapeHtml(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
        }
    }
}
