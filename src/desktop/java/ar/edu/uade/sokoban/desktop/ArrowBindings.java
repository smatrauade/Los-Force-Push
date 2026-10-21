package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/** Window-scoped bindings do not depend on the board holding keyboard focus. */
public final class ArrowBindings {
    private ArrowBindings() { }

    public static void install(JComponent target, Consumer<Direction> onDirection) {
        for (Direction direction : Direction.values()) {
            String action = "move-" + direction.name();
            target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(direction.name()), action);
            target.getActionMap().put(action, new AbstractAction() {
                private static final long serialVersionUID = 1L;
                @Override
                public void actionPerformed(ActionEvent event) {
                    onDirection.accept(direction);
                }
            });
        }
    }
}
