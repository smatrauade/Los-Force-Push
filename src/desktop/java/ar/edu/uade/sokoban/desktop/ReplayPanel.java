package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.replay.ReplayPlayer;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Detached EDT playback. No GameController, GameSession, score, SFX or progression. */
final class ReplayPanel extends JPanel implements AutoCloseable {
    private static final long serialVersionUID = 1L;
    private final BoardPanel board = new BoardPanel();
    private final JLabel progress = new JLabel("REPLAY");
    private final JButton play = PixelTheme.button("PLAY");
    private final JButton pause = PixelTheme.button("PAUSE");
    private final JButton step = PixelTheme.button("STEP");
    private final JButton reset = PixelTheme.button("RESET");
    private transient ReplayPlayer player;
    private transient ReplayTrace trace;
    private transient Timer timer;
    private long generation;

    ReplayPanel(Runnable back) {
        super(new BorderLayout());
        Objects.requireNonNull(back, "back");
        progress.setFont(PixelTheme.BODY);
        progress.setForeground(PixelTheme.CREAM);
        add(progress, BorderLayout.NORTH);
        add(new JScrollPane(board), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout());
        for (JButton button : new JButton[]{play, pause, step, reset}) { controls.add(button); }
        JButton backButton = PixelTheme.button("BACK");
        controls.add(backButton);
        add(controls, BorderLayout.SOUTH);
        play.addActionListener(event -> play());
        pause.addActionListener(event -> pause());
        step.addActionListener(event -> step());
        reset.addActionListener(event -> reset());
        backButton.addActionListener(event -> { close(); back.run(); });
        updateControls();
    }

    BoardPanel board() { return board; }
    int index() { return player == null ? 0 : player.index(); }
    boolean isPlaying() { return timer != null && timer.isRunning(); }
    String progressText() { return progress.getText(); }

    boolean open(ReplayTrace next) {
        requireEdt();
        Objects.requireNonNull(next, "trace");
        close();
        ReplayPlayer candidate = new ReplayPlayer(next);
        if (!candidate.verify()) {
            progress.setText("REPLAY NO DISPONIBLE: LA TRAZA NO COINCIDE");
            return false;
        }
        trace = next;
        player = candidate;
        publish();
        return true;
    }

    void play() {
        requireEdt();
        if (player == null || !player.hasNext() || isPlaying()) { return; }
        long token = ++generation;
        timer = new Timer(300, event -> {
            if (token == generation) { advance(); }
        });
        timer.start();
        updateControls();
    }

    void pause() {
        requireEdt();
        generation++;
        if (timer != null) { timer.stop(); timer = null; }
        updateControls();
    }

    void step() {
        requireEdt();
        pause();
        advance();
    }

    private void advance() {
        if (player == null || !player.hasNext()) { pause(); return; }
        try {
            board.setFacing(player.nextDirection());
            player.step();
            if (!player.hasNext() && (!player.currentState().equals(trace.expectedFinalState())
                    || player.moves() != trace.expectedMoves() || player.pushes() != trace.expectedPushes())) {
                throw new IllegalStateException("Replay final mismatch");
            }
            publish();
            if (!player.hasNext()) { pause(); }
        } catch (IllegalStateException | IllegalArgumentException failure) {
            close();
            progress.setText("REPLAY NO DISPONIBLE: LA TRAZA NO COINCIDE");
        }
    }

    void reset() {
        requireEdt();
        pause();
        if (player != null) { player.reset(); board.setFacing(ar.edu.uade.sokoban.movement.Direction.DOWN); publish(); }
    }

    private void publish() {
        board.showState(player.currentState());
        // Plain text avoids interpreting external filenames or IDs as HTML.
        progress.setText("REPLAY  /  " + trace.levelContext() + "  /  PASO " + player.index()
                + "/" + trace.commands().size() + "  /  EMPUJES " + player.pushes());
        updateControls();
    }

    private void updateControls() {
        boolean hasNext = player != null && player.hasNext();
        play.setEnabled(hasNext && !isPlaying());
        step.setEnabled(hasNext && !isPlaying());
        pause.setEnabled(isPlaying());
        reset.setEnabled(player != null);
    }

    @Override public void close() {
        requireEdt();
        pause();
        board.cancelAnimation();
        player = null;
        trace = null;
        updateControls();
    }

    private static void requireEdt() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Replay presentation requires the EDT");
        }
    }
}
