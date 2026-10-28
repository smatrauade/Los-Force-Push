# Contrato de integracion entre Punto 5 y los otros modulos

Esta entrega toca exclusivamente presentacion, eventos UI y audio; no introduce
reglas de cajas, score, guardado, Undo ni progresion. Los archivos base afectados
son los del Swing experimental; hay que reconciliarlos si otra rama tambien los
modifico. Estado de referencia: ZIP recibido del proyecto al 26/10/2026.

## Clases y limites

| Clase | Contrato nuevo | Limite |
|---|---|---|
| `BoardPanel` | Recorre `Board.rows()` y pinta capas terrain -> goal -> occupant | Nunca muta el tablero ni decide movimiento. |
| `PixelAssets` | Carga bajo `/assets/sprites/*.png`, cachea imagenes | Requiere recursos en classpath. |
| `PixelTheme` | Paleta, tipografia y botones Swing | Sin dominio. |
| `SoundManager` | Reproduce WAVs originales y soporta mute/error de dispositivo | No se invoca desde `paintComponent`. |
| `GameView` | Mantiene `render`, `showLoadError`; agrega metodos default `onMove` y `showVictory` | Las sondas de tests existentes siguen compilando. |
| `LevelResult` | DTO de datos finales (nivel, moves, pushes, undoUses, score) | No calcula score. |
| `SwingApplication` | Cards MENU/GAME/PAUSE/OPTIONS/HELP/FINISH | Solo captura acciones y representa proyecciones. |
| `GameController` | Avisa de `MoveResult.Kind` a la vista tras movimiento | No agrega regla de gameplay. |
| `UiCommands` | Callbacks optativos para Undo, Restart, Save, Load y Next | Todos null por defecto; la UI nunca implementa el comando. |

## Para agregar tipos futuros

**Hoy** `Terrain` solo define `WALL/FLOOR/GOAL` y `Occupant` solo
`EMPTY/PLAYER/BOX`. Por compatibilidad, `BoardPanel` consulta `enum.name()` en un
adaptador de presentacion para sugerencias `ICE`, `LOCK`, `GATE_*`,
`KEY_BOX` y `FRAGILE_BOX`. **Esos nombres no constituyen una especificacion de
modelo.** Cuando el equipo concrete su contrato, reemplazar/ajustar la funcion
`spriteForTerrain` y `spriteForOccupant` segun las clases y DTO que realmente
existan. No forzar nuevos enums ni derivar la resistencia de la caja desde Swing.

Para fragiles: el motor/DTO debe informar resistencia efectiva. Asociar
`box_fragile_3`, `box_fragile_2`, `box_fragile_1`, `box_fragile_0` al estado
normalizado, sin imponer un valor inicial en el modelo. Para locks/portones usar
`lock/lock_active`, `gate_closed/gate_open` segun estado del core. Para hielo usar
`ice` **solo para representar**, no calcular deslizamiento en BoardPanel.

## Para integrar el Punto 4 - Undo, Restart y score

- `undoButton` esta visible y deshabilitado, y `Z` informa que falta ese modulo.
  El responsable de Undo debe agregar al controller un comando de sesion
  que restaure el snapshot correcto y volver a llamar a `GameView.render`.
  Habilitar Undo solo cuando el modulo indique disponibilidad real (el documento
  oficial pide 5 movimientos por uso, 15 de historia y maximo 3 usos consecutivos).
- El boton **Restart** y la tecla **R** estan preparados pero inactivos. El modulo
  del Punto 4 debe proveer el callback y la confirmacion/politica de sesion;
  la interfaz NO recarga TXT ni reinicia metricas por su cuenta.
- `moves` y `pushes` del HUD vienen de `GamePresentation`, no del panel.
- El score se recibe a traves de `LevelResult` desde el servicio de score,
  con sus valores ya calculados. No elegir una formula dentro de la vista.

## Para integrar el Punto 3 - victoria, niveles, locks

- Cuando la sesion confirme victoria, emitir `view.showVictory(new LevelResult(...))`.
- `FINISH` ya muestra nivel, movimientos, empujes, Undo y score.
- El boton `SIGUIENTE NIVEL` permanece deshabilitado. Habilitarlo solo cuando el
  catalogo de niveles entregue un destino y conectar al controller (no a
  LevelLoader directamente desde el panel de victoria).
- No inferir victoria haciendo `boxCount == goalCount`: cantidad igual no prueba
  que todas las cajas esten en destino.

## Para la funcionalidad adicional de guardado y recuperacion

- `GUARDAR PARTIDA` y `CARGAR PARTIDA` son botones preparados, **no operativos**.
- Conectar las acciones al servicio de persistencia una vez definido el contrato
  del equipo y la semantica de restauracion de Undo/metricas/estados especiales.
- Mostrar excepciones con un dialogo de error legible y preservar sesion actual
  si falla la lectura de un archivo guardado.

## Eventos y sonido

`GameController.move` llama a `view.onMove` para `MOVED`, `PUSHED` y `BLOCKED`.
La implementacion Swing dispara `step`, `push` o `blocked`. La logica futura puede
emitir eventos tipados `FRAGILE`, `ICE`, `LOCK`, `GATE`, `GOAL`, `UNDO`, `VICTORY`.
Los WAV estan en `src/desktop/resources/assets/sfx`, son cortos, mono PCM.
No crear un `SoundManager` global en el core ni usar sonido en renderer.

## Decisiones visuales

- 32x32 RGBA, ampliacion entera 1x/2x/3x (`nearest-neighbor`).
- Fondo oscuro y HUD cobrizo; piso crema y paredes terracota; objetivo dorado;
  hielo cian; caja llave azul metalizada; dano fragile rojo.
- Cada archivo sprite es individual; el generador Python opcional recrea solo los WAV y preserva los PNG finales.
- `ImageIO` y `javax.sound.sampled` ya vienen con Java 17 (`java.desktop`).
- Si el equipo migra a Maven/Gradle, registrar `src/desktop/resources` como recursos.

## Preguntas de arquitectura en defensa

¿Que aporta GameView? Permite pruebas sin JFrame y desacopla el controller de Swing.
¿Que aporta PixelAssets? Cachea recursos y centraliza validacion.
¿Como se respeta MVC? Board inmutable / motor en core, coordinacion en controller,
pintura, teclado y audio en modulo desktop.
¿Por que no se agrega Singleton? No existe necesidad demostrada de estado global de
sonido. ¿Como evoluciona la UI? Adaptando DTO/renderer al contrato confirmado,
sin hardcodear reglas nuevas ni convertir Swing en modelo.

## Conectar comandos sin tocar el renderer

El controlador/sesion que construya el equipo puede llamar al metodo nuevo (default)
`GameView.configureUiCommands` en el EDT, **cuando tenga comandos realmente operativos**:

```java
view.configureUiCommands(new UiCommands(
    () -> session.undoFiveMoves(),
    () -> session.requestRestart(),
    () -> persistence.save(session.snapshot()),
    () -> persistence.restore(),
    () -> levelFlow.nextLevel()));
```

**El fragmento es pseudocodigo de integracion**, NO son clases ni metodos actuales
(`session`, `persistence` y `levelFlow` todavia no existen en el ZIP).
Un parametro `null` significa que el boton queda deshabilitado. Para actualizar
la disponibilidad tras cambios de sesion, llamar otra vez a `configureUiCommands`.
`R` y `Z` invocan el mismo callback que los botones. La UI no agrega logica de
persistencia, Undo, restart ni progreso. Ejecutar callbacks breves en EDT y
trasladar E/S lenta a un servicio apropiado fuera del EDT.

Para sonidos de futuros eventos:
`view.playEffect(SoundManager.Effect.GOAL)` o `.FRAGILE`, `.ICE`, `.LOCK`, `.GATE`
cuando el motor notifique un **evento real**, no desde `paintComponent`.
