package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;

/**
 * Headless acceptance for the I3.C presentation contract: authoritative ICE
 * trajectories, Fragile break after the last cell, SFX ownership, victory
 * deferral and safe cancellation without mutating gameplay state.
 */
public final class AnimationPresentationTest {
    private static final int MANUAL_MILLIS = 60_000;
    private static final String OPEN =
            "#####\n#   #\n# @ #\n#   #\n#####\n";
    private static final String BLOCKED =
            "#####\n#@  #\n#####\n";
    private static final String PUSH_ONE_CELL =
            "######\n#@$  #\n######\n";
    private static final String FRAGILE_ICE_ONE =
            "!sokoban-txt 2\n[grid]\n"
            + "# # # # # # #\n"
            + "# @ {floor|fragile:f1} {ice|empty} {ice|empty} . #\n"
            + "# # # # # # #\n[attributes]\n"
            + "fragile f1 resistance=1\n";
    private static final String FRAGILE_SINGLE_CELL =
            "!sokoban-txt 2\n[grid]\n"
            + "# # # # #\n"
            + "# @ {floor|fragile:f1} . #\n"
            + "# # # # #\n[attributes]\n"
            + "fragile f1 resistance=1\n";
    private static final String FRAGILE_ICE_TWO =
            "!sokoban-txt 2\n[grid]\n"
            + "# # # # # # #\n"
            + "# @ {floor|fragile:f1} {ice|empty} {ice|empty} _ #\n"
            + "# # # # # # #\n[attributes]\n"
            + "fragile f1 resistance=2\n";
    private static final String CANCEL_ICE =
            "!sokoban-txt 2\n[grid]\n"
            + "# # # # # # # #\n"
            + "# _ _ _ _ _ . #\n"
            + "# _ @ $ {ice|empty} {ice|empty} # #\n"
            + "# _ _ _ _ _ _ #\n"
            + "# # # # # # # #\n[attributes]\n";

    private static int passed;
    private static int failed;
    private static Path levelsDirectory;
    private static Path spritesDirectory;
    private static Path workDirectory;
    private static Map<String, String> spritesBefore;

    private AnimationPresentationTest() { }

    @FunctionalInterface
    private interface Check { void run() throws Exception; }

    private static final class Probe implements GameView {
        private GamePresentation presentation;
        private String error;
        private UiCommands commands = UiCommands.EMPTY;
        private LevelResult victory;
        private final List<String> events = new ArrayList<>();
        private final List<BoxAnimation> animations = new ArrayList<>();
        private final List<SoundManager.Effect> effects = new ArrayList<>();
        private final List<MoveResult.Kind> moves = new ArrayList<>();

        @Override
        public void render(GamePresentation value) {
            presentation = value;
            events.add("RENDER");
        }

        @Override
        public void showLoadError(String value) {
            error = value;
        }

        @Override
        public void configureUiCommands(UiCommands value) {
            commands = value;
        }

        @Override
        public void showVictory(LevelResult value) {
            victory = value;
            events.add("VICTORY");
        }

        @Override
        public void animateBox(BoxAnimation animation) {
            animations.add(animation);
            events.add("ANIMATE");
        }

        @Override
        public void onMove(MoveResult.Kind kind) {
            moves.add(kind);
            events.add("MOVE:" + kind);
        }

        @Override
        public void playEffect(SoundManager.Effect effect) {
            effects.add(effect);
            events.add("SFX:" + effect);
        }
    }

    private static final class BoardProbe implements GameView {
        private final BoardPanel panel = new BoardPanel();
        private final List<BoxAnimation> animations = new ArrayList<>();
        private final AtomicInteger completions = new AtomicInteger();
        private GamePresentation presentation;
        private String error;
        private LevelResult victory;

        @Override
        public void render(GamePresentation value) {
            presentation = value;
            panel.showState(value.state());
        }

        @Override
        public void showLoadError(String value) {
            error = value;
        }

        @Override
        public void animateBox(BoxAnimation animation) {
            animations.add(animation);
            panel.playBoxAnimation(animation, MANUAL_MILLIS, completions::incrementAndGet);
        }

        @Override
        public void showVictory(LevelResult value) {
            victory = value;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Se esperaban niveles y sprites");
        }
        levelsDirectory = Path.of(args[0]);
        spritesDirectory = Path.of(args[1]);
        spritesBefore = spriteHashes();
        workDirectory = Files.createTempDirectory("sokoban-animation-tests-");
        try {
            test("trayectoria ICE exacta sin caja fantasma",
                    AnimationPresentationTest::iceTrajectory);
            test("Fragile 1 sobre ICE rompe tras la ultima celda",
                    AnimationPresentationTest::fragileShatterOnIce);
            test("Fragile 1 sin hielo anima una celda antes de romper",
                    AnimationPresentationTest::fragileSingleCell);
            test("Fragile 2 conserva ID y resistencia durante el deslizamiento",
                    AnimationPresentationTest::fragilePreservesMetadata);
            test("KeyBox con Lock y muro dinamico conserva metadatos",
                    AnimationPresentationTest::keyBoxKeepsLockMetadata);
            test("bloqueo o movimiento sin empuje no anima",
                    AnimationPresentationTest::nonPushDoesNotAnimate);
            test("ICE y FRAGILE suenan una vez sin duplicar PUSH o GOAL",
                    AnimationPresentationTest::soundOwnership);
            test("publica el runtime final antes de animar y victoria headless inmediata",
                    AnimationPresentationTest::publicationThenAnimation);
            test("la victoria final espera el fin de la animacion",
                    AnimationPresentationTest::victoryDeferral);
            test("Undo Reiniciar y recarga cancelan la animacion",
                    AnimationPresentationTest::controllerCancellation);
            test("pausa cierre y reemplazo no dejan callbacks obsoletos",
                    AnimationPresentationTest::explicitCancellation);
            test("una sola animacion y temporizador no bloqueante en EDT",
                    AnimationPresentationTest::singleTimerAndEdtCompletion);
            test("tema y zoom durante animacion conservan mundo y restauran original",
                    AnimationPresentationTest::themeAndZoomDuringAnimation);
            test("sprites existentes sin PNG nuevos ni cambios de hash",
                    AnimationPresentationTest::spritesIntact);
        } finally {
            try (var paths = Files.walk(workDirectory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
        System.out.println("ANIMATION RESULT: " + passed + " PASS, " + failed + " FAIL");
        if (failed != 0) {
            System.exit(1);
        }
    }

    private static void iceTrajectory() throws Exception {
        RuntimeMoveResult push = iceWinningPush(loadState(levelsDirectory.resolve("ice_demo.txt")));
        List<Position> expectedPath = List.of(new Position(2, 3), new Position(2, 4),
                new Position(2, 5), new Position(2, 6), new Position(2, 7));
        check(push.kind() == MoveResult.Kind.PUSHED, "ICE push was not authoritative");
        check(push.boxPath().equals(expectedPath), "runtime boxPath changed");
        check(!push.boxShattered(), "normal ICE box cannot shatter");
        BoxAnimation animation = BoxAnimation.from(push);
        check(animation.needsFrames(), "ICE path should animate");

        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, null);
            check(panel.isAnimating(), "ICE animation did not start");
            List<BoardPanel.BoxFrame> expected = List.of(
                    new BoardPanel.BoxFrame(new Position(2, 3), "box"),
                    new BoardPanel.BoxFrame(new Position(2, 4), "box"),
                    new BoardPanel.BoxFrame(new Position(2, 5), "box"),
                    new BoardPanel.BoxFrame(new Position(2, 6), "box"),
                    new BoardPanel.BoxFrame(new Position(2, 7), "box_goal"));
            check(panel.animationFrames().equals(expected), "ICE frame order differs from boxPath");

            BoardPanel finalPanel = new BoardPanel();
            finalPanel.showState(push.state());
            BufferedImage finalImage = render(finalPanel);
            Rectangle destination = panel.cellBounds(animation.destination());
            int last = expected.size() - 1;
            for (int index = 0; index <= last; index++) {
                BoardPanel.BoxFrame frame = panel.projectedBoxFrame();
                check(frame != null && frame.equals(expected.get(index)),
                        "projected ICE frame is out of order");
                BufferedImage current = render(panel);
                if (index < last) {
                    check(!sameRegion(current, finalImage, destination),
                            "ghost box remained at the final cell before arrival");
                } else {
                    check(sameRegion(current, finalImage, destination),
                            "final destination did not show exactly one box");
                }
                boolean advanced = panel.advanceAnimationFrame();
                check(advanced == (index < last), "ICE frame advancement mismatch");
            }
            check(!panel.isAnimating(), "ICE animation did not finish");
            check(panel.animationFrames().isEmpty(), "finished frames were retained");
        });
    }

    private static void fragileShatterOnIce() throws Exception {
        Path fixture = write("fragile_ice_one.txt", FRAGILE_ICE_ONE);
        RuntimeMoveResult push = pushRight(loadState(fixture));
        List<Position> expectedPath = List.of(new Position(1, 3), new Position(1, 4),
                new Position(1, 5));
        check(push.boxShattered(), "Fragile resistance 1 did not break");
        check(push.boxPath().equals(expectedPath), "Fragile ICE path changed");
        check(push.state().boxAt(new Position(1, 5)) == null, "broken box remains in final world");
        BoxAnimation animation = BoxAnimation.from(push);
        check(animation.box().id().equals("f1") && animation.box().resistance() == 1,
                "Fragile identity was lost");

        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, null);
            List<BoardPanel.BoxFrame> expected = List.of(
                    new BoardPanel.BoxFrame(new Position(1, 3), "box_fragile_1"),
                    new BoardPanel.BoxFrame(new Position(1, 4), "box_fragile_1"),
                    new BoardPanel.BoxFrame(new Position(1, 5), "box_fragile_1"),
                    new BoardPanel.BoxFrame(new Position(1, 5), "box_broken"),
                    new BoardPanel.BoxFrame(new Position(1, 5), null));
            check(panel.animationFrames().equals(expected),
                    "Fragile must reach the last cell before breaking");
            BoardPanel finalPanel = new BoardPanel();
            finalPanel.showState(push.state());
            BufferedImage finalImage = render(finalPanel);
            Rectangle lastCell = panel.cellBounds(new Position(1, 5));
            for (int index = 0; index < expected.size(); index++) {
                check(panel.projectedBoxFrame().equals(expected.get(index)),
                        "Fragile frame out of order at " + index);
                boolean boxOnLastCell = index == 2 || index == 3;
                BufferedImage current = render(panel);
                if (boxOnLastCell) {
                    check(!sameRegion(current, finalImage, lastCell),
                            "broken box was repainted from the final world");
                } else {
                    check(sameRegion(current, finalImage, lastCell),
                            "Fragile absence or approach frame differs from the final world");
                }
                panel.advanceAnimationFrame();
            }
            check(!panel.isAnimating(), "Fragile animation did not finish");
        });
    }

    private static void fragileSingleCell() throws Exception {
        Path fixture = write("fragile_single.txt", FRAGILE_SINGLE_CELL);
        RuntimeMoveResult push = pushRight(loadState(fixture));
        check(push.boxShattered() && push.boxPath().equals(List.of(new Position(1, 3))),
                "single-cell Fragile path was not preserved");
        BoxAnimation animation = BoxAnimation.from(push);
        check(animation.needsFrames(), "a Fragile break must animate even with one cell");
        List<BoardPanel.BoxFrame> expected = List.of(
                new BoardPanel.BoxFrame(new Position(1, 3), "box_fragile_1"),
                new BoardPanel.BoxFrame(new Position(1, 3), "box_broken"),
                new BoardPanel.BoxFrame(new Position(1, 3), null));
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, null);
            check(panel.animationFrames().equals(expected), "single-cell break sequence differs");
            for (int index = 0; index < expected.size(); index++) {
                check(panel.projectedBoxFrame().equals(expected.get(index)),
                        "single-cell frame out of order");
                panel.advanceAnimationFrame();
            }
            check(!panel.isAnimating(), "single-cell animation did not finish");
        });
    }

    private static void fragilePreservesMetadata() throws Exception {
        Path fixture = write("fragile_ice_two.txt", FRAGILE_ICE_TWO);
        RuntimeMoveResult push = pushRight(loadState(fixture));
        check(push.kind() == MoveResult.Kind.PUSHED && !push.boxShattered(),
                "Fragile resistance 2 must survive one push");
        check(push.boxPath().equals(List.of(new Position(1, 3), new Position(1, 4),
                new Position(1, 5))), "Fragile slide path changed");
        BoxAnimation animation = BoxAnimation.from(push);
        check(animation.box().kind() == RuntimeState.BoxKind.FRAGILE
                && animation.box().id().equals("f1") && animation.box().resistance() == 2,
                "animation lost original Fragile metadata");
        RuntimeState.Box finalBox = push.state().boxAt(new Position(1, 5));
        check(finalBox != null && finalBox.id().equals("f1") && finalBox.resistance() == 1,
                "final runtime did not retain Fragile identity and remaining resistance");
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, null);
            for (BoardPanel.BoxFrame frame : panel.animationFrames()) {
                check("box_fragile_2".equals(frame.sprite()),
                        "animation used the reduced resistance instead of the box value");
            }
            panel.cancelAnimation();
            check(panel.shownState() == push.state(), "animation replaced the runtime world");
        });
    }

    private static void keyBoxKeepsLockMetadata() throws Exception {
        RuntimeMoveResult push = pushRight(keyLockState());
        check(push.kind() == MoveResult.Kind.PUSHED, "KeyBox push was blocked");
        check(push.boxPath().equals(List.of(new Position(1, 3), new Position(1, 4),
                new Position(1, 5))), "KeyBox ICE path changed");
        RuntimeState finalState = push.state();
        RuntimeState.Box box = finalState.boxAt(new Position(1, 5));
        check(box != null && box.kind() == RuntimeState.BoxKind.KEY && box.id().equals("k1"),
                "KeyBox metadata was lost");
        check(finalState.locks().equals(List.of(new RuntimeState.Lock("l1", new Position(1, 6))))
                && finalState.walls().equals(List.of(new RuntimeState.DynamicWall(
                        "w1", new Position(1, 7), Set.of("l1")))),
                "Lock or dynamic wall metadata changed");
        check(!finalState.lockActive("l1") && !finalState.wallOpen(new Position(1, 7)),
                "unrelated Lock state changed");
        BoxAnimation animation = BoxAnimation.from(push);
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(finalState);
            panel.playBoxAnimation(animation, MANUAL_MILLIS, null);
            check(panel.animationFrames().size() == 3, "KeyBox frame count changed");
            for (BoardPanel.BoxFrame frame : panel.animationFrames()) {
                check("box_key".equals(frame.sprite()), "KeyBox sprite was degraded");
            }
            check(panel.shownState().locks().equals(finalState.locks())
                    && panel.shownState().walls().equals(finalState.walls()),
                    "animation mutated Lock or dynamic wall metadata");
            panel.cancelAnimation();
        });
    }

    private static void nonPushDoesNotAnimate() throws Exception {
        Probe blocked = new Probe();
        GameController blockedGame = new GameController(blocked);
        blockedGame.load(write("blocked.txt", BLOCKED));
        blocked.events.clear();
        blockedGame.move(Direction.UP);
        check(blocked.events.equals(List.of("MOVE:BLOCKED")), "blocked movement emitted more events");

        Probe moved = new Probe();
        GameController movedGame = new GameController(moved);
        movedGame.load(write("moved.txt", OPEN));
        moved.events.clear();
        movedGame.move(Direction.RIGHT);
        check(moved.events.equals(List.of("MOVE:MOVED", "RENDER")),
                "plain movement emitted an animation");
        check(moved.animations.isEmpty(), "plain movement collected animation");

        Probe pushed = new Probe();
        GameController pushedGame = new GameController(pushed);
        pushedGame.load(write("push_one.txt", PUSH_ONE_CELL));
        pushed.events.clear();
        pushedGame.move(Direction.RIGHT);
        check(pushed.events.equals(List.of("MOVE:PUSHED", "RENDER")),
                "single-cell push without ICE or Fragile should not animate");
        check(pushed.animations.isEmpty(), "single-cell push collected animation");
    }

    private static void soundOwnership() throws Exception {
        Probe ice = new Probe();
        GameController iceGame = new GameController(ice);
        iceGame.load(levelsDirectory.resolve("ice_demo.txt"));
        iceGame.move(Direction.DOWN);
        ice.effects.clear();
        ice.moves.clear();
        iceGame.move(Direction.RIGHT);
        check(count(ice.effects, SoundManager.Effect.ICE) == 1, "ICE SFX was not emitted once");
        check(count(ice.effects, SoundManager.Effect.FRAGILE) == 0, "ICE emitted Fragile SFX");
        check(count(ice.effects, SoundManager.Effect.GOAL) == 1, "GOAL SFX was not emitted once");
        check(count(ice.effects, SoundManager.Effect.PUSH) == 0,
                "controller duplicated PUSH outside onMove");
        check(count(ice.moves, MoveResult.Kind.PUSHED) == 1,
                "onMove did not receive exactly one push");

        Probe shatter = new Probe();
        GameController shatterGame = new GameController(shatter);
        shatterGame.load(write("sound_shatter.txt", FRAGILE_ICE_ONE));
        shatterGame.move(Direction.RIGHT);
        check(count(shatter.effects, SoundManager.Effect.ICE) == 1,
                "Fragile slide did not emit ICE once");
        check(count(shatter.effects, SoundManager.Effect.FRAGILE) == 1,
                "Fragile break did not emit FRAGILE once");
        check(count(shatter.effects, SoundManager.Effect.GOAL) == 0,
                "shattered box must not emit GOAL");
        check(count(shatter.moves, MoveResult.Kind.PUSHED) == 1,
                "shatter push duplicated onMove");

        Probe single = new Probe();
        GameController singleGame = new GameController(single);
        singleGame.load(write("sound_single.txt", FRAGILE_SINGLE_CELL));
        singleGame.move(Direction.RIGHT);
        check(count(single.effects, SoundManager.Effect.ICE) == 0,
                "single-cell Fragile emitted ICE");
        check(count(single.effects, SoundManager.Effect.FRAGILE) == 1,
                "single-cell Fragile did not emit FRAGILE once");
        check(count(single.effects, SoundManager.Effect.GOAL) == 0,
                "single-cell Fragile emitted GOAL");
    }

    private static void publicationThenAnimation() throws Exception {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levelsDirectory.resolve("ice_demo.txt"));
        game.move(Direction.DOWN);
        view.events.clear();
        game.move(Direction.RIGHT);
        check(view.events.equals(List.of("MOVE:PUSHED", "SFX:ICE", "SFX:GOAL",
                "RENDER", "ANIMATE", "VICTORY")), "publish/animate/victory order changed");
        check(view.presentation.state().boxAt(new Position(2, 7)) != null,
                "final runtime was not published before the animation");
        check(view.victory != null, "headless victory was not immediate on return");
        check(view.victory.moves() == 2 && view.victory.pushes() == 1
                && view.victory.score() == 996, "headless score or counters changed");
        check(view.animations.size() == 1
                && view.animations.get(0).path().equals(List.of(new Position(2, 3),
                        new Position(2, 4), new Position(2, 5), new Position(2, 6),
                        new Position(2, 7))), "animation did not use the published path");
    }

    private static void victoryDeferral() throws Exception {
        RuntimeMoveResult push = iceWinningPush(loadState(levelsDirectory.resolve("ice_demo.txt")));
        BoxAnimation animation = BoxAnimation.from(push);
        LevelResult result = new LevelResult("ice_demo.txt", 2, 1, 0, 996);
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            VictoryGate gate = new VictoryGate();
            AtomicReference<LevelResult> presented = new AtomicReference<>();
            check(gate.defer(result, true), "winning move was not deferred");
            panel.playBoxAnimation(animation, MANUAL_MILLIS, () -> {
                LevelResult pending = gate.take();
                if (pending != null) {
                    presented.set(pending);
                }
            });
            check(presented.get() == null, "finish screen appeared during the animation");
            while (panel.advanceAnimationFrame()) {
                // Advance deterministically instead of depending on wall-clock time.
            }
            check(presented.get() == result, "deferred victory was not released");
            check(gate.take() == null, "deferred victory was released twice");
        });
        VictoryGate immediate = new VictoryGate();
        check(!immediate.defer(result, false), "non-animated victory was deferred");
        check(immediate.take() == null, "non-animated victory left pending state");
    }

    private static void controllerCancellation() throws Exception {
        Path cancellation = write("cancel_ice.txt", CANCEL_ICE);
        Path replacement = write("replacement.txt", OPEN);
        onEdt(() -> {
            BoardProbe view = new BoardProbe();
            GameController game = new GameController(view);
            game.load(cancellation);
            check(view.presentation != null && view.error == null, "cancellation fixture failed");
            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.RIGHT);
            check(view.animations.size() == 1 && view.panel.isAnimating(),
                    "ICE animation did not start before Undo");
            game.undo();
            check(!view.panel.isAnimating() && view.panel.animationFrames().isEmpty(),
                    "Undo did not cancel the animation");
            check(view.presentation.moves() == 0 && game.undoUsesTotal() == 1,
                    "Undo did not restore the runtime");
            check(view.completions.get() == 0, "cancelled Undo ran a completion callback");

            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.RIGHT);
            check(view.animations.size() == 2 && view.panel.isAnimating(),
                    "second ICE animation did not start before Restart");
            game.restart();
            check(!view.panel.isAnimating() && view.presentation.moves() == 0,
                    "Restart did not cancel the animation and reset counters");

            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.UP);
            game.move(Direction.DOWN);
            game.move(Direction.RIGHT);
            check(view.animations.size() == 3 && view.panel.isAnimating(),
                    "third ICE animation did not start before reload");
            game.load(replacement);
            check(!view.panel.isAnimating(), "reload did not cancel the animation");
            check(view.panel.shownState() == view.presentation.state(),
                    "reload did not publish the replacement world");
            check(view.completions.get() == 0, "cancelled animations ran stale callbacks");
        });
    }

    private static void explicitCancellation() throws Exception {
        RuntimeMoveResult push = pushRight(keyLockState());
        BoxAnimation animation = BoxAnimation.from(push);
        AtomicInteger completions = new AtomicInteger();
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, 5, completions::incrementAndGet);
            check(panel.cancelAnimation(), "explicit cancellation reported no animation");
            check(!panel.isAnimating() && !panel.hasAnimationCompletion(),
                    "explicit cancellation retained timer or callback");
            panel.showState(push.state());
            check(!panel.isAnimating(), "showState left an animation running");
            panel.playBoxAnimation(animation, 5, completions::incrementAndGet);
            panel.showState(push.state());
            check(!panel.hasAnimationCompletion(), "showState retained a stale callback");
            VictoryGate gate = new VictoryGate();
            check(gate.defer(new LevelResult("nivel.txt", 1, 1, 0, 998), true),
                    "gate did not defer");
            gate.clear();
            check(gate.take() == null, "gate clear retained a victory");
        });
        TimeUnit.MILLISECONDS.sleep(150);
        onEdt(() -> { });
        check(completions.get() == 0, "queued timer event invoked a cancelled callback");
    }

    private static void singleTimerAndEdtCompletion() throws Exception {
        RuntimeMoveResult push = pushRight(keyLockState());
        BoxAnimation animation = BoxAnimation.from(push);
        AtomicReference<Thread> edtThread = new AtomicReference<>();
        AtomicReference<Thread> callbackThread = new AtomicReference<>();
        AtomicReference<BoardPanel> panelRef = new AtomicReference<>();
        AtomicInteger first = new AtomicInteger();
        AtomicInteger second = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(1);
        onEdt(() -> {
            edtThread.set(Thread.currentThread());
            BoardPanel panel = new BoardPanel();
            panelRef.set(panel);
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, first::incrementAndGet);
            List<BoardPanel.BoxFrame> frames = panel.animationFrames();
            panel.playBoxAnimation(animation, 5, () -> {
                second.incrementAndGet();
                callbackThread.set(Thread.currentThread());
                done.countDown();
            });
            check(panel.isAnimating(), "replacement animation did not start");
            check(first.get() == 0 && panel.hasAnimationCompletion(),
                    "previous animation was not replaced");
            check(panel.animationFrames().equals(frames),
                    "replacement animation changed the deterministic frames");
        });
        check(done.await(5, TimeUnit.SECONDS), "Swing timer did not complete");
        onEdt(() -> {
            check(panelRef.get() != null && !panelRef.get().isAnimating(),
                    "completed timer still reports animation");
        });
        check(first.get() == 0, "replaced animation invoked a stale completion");
        check(second.get() == 1, "timer completion did not run exactly once");
        check(callbackThread.get() == edtThread.get(), "completion did not run on EDT");
    }

    private static void themeAndZoomDuringAnimation() throws Exception {
        RuntimeMoveResult push = pushRight(keyLockState());
        BoxAnimation animation = BoxAnimation.from(push);
        AtomicInteger completions = new AtomicInteger();
        onEdt(() -> {
            BoardPanel panel = new BoardPanel();
            panel.showState(push.state());
            panel.playBoxAnimation(animation, MANUAL_MILLIS, completions::incrementAndGet);
            RuntimeState world = panel.shownState();
            List<BoardPanel.BoxFrame> frames = panel.animationFrames();
            int frame = panel.animationFrameIndex();
            BufferedImage original = render(panel);

            panel.setTheme(VisualThemes.byId(VisualThemes.NIGHT_ID));
            BufferedImage night = render(panel);
            check(!samePixels(original, night), "theme change did not repaint the animation");
            check(panel.shownState() == world && panel.animationFrames().equals(frames)
                    && panel.animationFrameIndex() == frame, "theme mutated gameplay or frames");

            panel.setScale(3);
            check(panel.getScale() == 3 && panel.shownState() == world,
                    "zoom mutated the runtime world");
            check(panel.animationFrames().equals(frames) && panel.isAnimating(),
                    "zoom interrupted or duplicated animation frames");

            panel.setTheme(VisualThemes.original());
            panel.setScale(2);
            BufferedImage restored = render(panel);
            check(samePixels(original, restored), "original theme did not restore exactly");
            panel.cancelAnimation();
            check(completions.get() == 0, "cancelled theme test ran a completion");
        });
    }

    private static void spritesIntact() throws Exception {
        check(spritesBefore.equals(spriteHashes()), "sprite PNG hashes changed during the suite");
        for (String name : List.of("box", "box_goal", "box_key", "box_fragile_1",
                "box_fragile_2", "box_broken", "ice", "goal", "goal_active",
                "lock", "gate_closed")) {
            check(spritesBefore.containsKey(name + ".png"), "missing existing sprite " + name);
            check(new PixelAssets().image(name).getWidth() == 32,
                    "sprite is not 32x32: " + name);
        }
    }

    private static RuntimeMoveResult iceWinningPush(RuntimeState initial) {
        RuntimeMovementEngine engine = new RuntimeMovementEngine();
        RuntimeState afterDown = engine.move(initial, Direction.DOWN).state();
        return engine.move(afterDown, Direction.RIGHT);
    }

    private static RuntimeState loadState(Path path) {
        Probe view = new Probe();
        new GameController(view).load(path);
        check(view.error == null && view.presentation != null,
                "failed to load " + path.getFileName());
        return view.presentation.state();
    }

    private static RuntimeMoveResult pushRight(RuntimeState state) {
        return new RuntimeMovementEngine().move(state, Direction.RIGHT);
    }

    private static RuntimeState keyLockState() {
        List<List<RuntimeState.Ground>> terrain = List.of(
                List.of(RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL),
                List.of(RuntimeState.Ground.WALL, RuntimeState.Ground.FLOOR,
                        RuntimeState.Ground.FLOOR, RuntimeState.Ground.ICE,
                        RuntimeState.Ground.ICE, RuntimeState.Ground.FLOOR,
                        RuntimeState.Ground.LOCK, RuntimeState.Ground.DYNAMIC_WALL),
                List.of(RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL,
                        RuntimeState.Ground.WALL, RuntimeState.Ground.WALL));
        return new RuntimeState(terrain, new Position(1, 1),
                List.of(RuntimeState.Box.key("k1", new Position(1, 2))),
                List.of(new RuntimeState.Lock("l1", new Position(1, 6))),
                List.of(new RuntimeState.DynamicWall("w1", new Position(1, 7),
                        Set.of("l1"))));
    }

    private static int count(List<SoundManager.Effect> effects, SoundManager.Effect wanted) {
        int total = 0;
        for (SoundManager.Effect effect : effects) {
            if (effect == wanted) {
                total++;
            }
        }
        return total;
    }

    private static int count(List<MoveResult.Kind> kinds, MoveResult.Kind wanted) {
        int total = 0;
        for (MoveResult.Kind kind : kinds) {
            if (kind == wanted) {
                total++;
            }
        }
        return total;
    }

    private static Path write(String name, String text) throws Exception {
        Path path = workDirectory.resolve(name);
        Files.writeString(path, text, StandardCharsets.UTF_8);
        return path;
    }

    private static BufferedImage render(Component component) {
        component.setSize(component.getPreferredSize());
        BufferedImage image = new BufferedImage(Math.max(1, component.getWidth()),
                Math.max(1, component.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            component.paint(graphics);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static boolean sameRegion(BufferedImage first, BufferedImage second, Rectangle bounds) {
        return Arrays.equals(region(first, bounds), region(second, bounds));
    }

    private static int[] region(BufferedImage image, Rectangle bounds) {
        return image.getRGB(bounds.x, bounds.y, bounds.width, bounds.height, null, 0, bounds.width);
    }

    private static boolean samePixels(BufferedImage first, BufferedImage second) {
        return first.getWidth() == second.getWidth()
                && first.getHeight() == second.getHeight()
                && Arrays.equals(pixels(first), pixels(second));
    }

    private static int[] pixels(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private static Map<String, String> spriteHashes() throws Exception {
        Map<String, String> result = new TreeMap<>();
        try (var files = Files.list(spritesDirectory)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".png")).toList()) {
                result.put(file.getFileName().toString(), sha256(file));
            }
        }
        return result;
    }

    private static String sha256(Path file) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
        StringBuilder text = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            text.append(Character.forDigit((value >> 4) & 0xF, 16));
            text.append(Character.forDigit(value & 0xF, 16));
        }
        return text.toString();
    }

    private static void onEdt(Check action) throws Exception {
        Throwable[] failure = new Throwable[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Throwable problem) {
                failure[0] = problem;
            }
        });
        if (failure[0] instanceof Exception exception) {
            throw exception;
        }
        if (failure[0] instanceof Error error) {
            throw error;
        }
        if (failure[0] != null) {
            throw new AssertionError(failure[0]);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void test(String name, Check check) {
        try {
            check.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Exception | AssertionError failure) {
            failed++;
            System.err.println("FAIL " + name + ": " + failure);
        }
    }
}
