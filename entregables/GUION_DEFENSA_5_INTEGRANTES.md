# Los Force Push / Sokoban — Guion de defensa

**Asignatura:** Procesos de Desarrollo de Software — UADE · Ingeniería en Informática
**Docente:** Jonathan Leonel Pepe
**Fecha de defensa:** 18 de noviembre de 2026

Guía de los temas, decisiones de diseño y funcionalidades de Sokoban, organizada por integrante y diapositiva.

La [presentación editable](Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx) contiene estas explicaciones ampliadas en sus notas del presentador. La [copia PDF](Los_Force_Push_Sokoban_Defensa_18-11-2026.pdf) reproduce las nueve diapositivas. La [tarjeta de demostración](TARJETA_OPERADOR_DEMO_18_MIN.txt) conserva un recorrido reproducible de referencia.

## 1. Distribución de temas

| Diapositiva | Integrante | Tema | Tiempo orientativo |
|---|---|---|---|
| 1 | Lucas Capatti | Introducción al proyecto | 00:00–00:25 |
| 2 | Lucas Capatti | MVC, carga TXT y construcción de niveles | 00:25–01:45 |
| 3 | Juan Ignacio Antonini | Cerrojos, muros, victoria y Observer | 01:45–05:30 |
| 4 | Santino Matra | Motor e inmutabilidad | 05:30–06:40 |
| 5 | Santino Matra | Movimiento, hielo, cajas frágiles y GRASP | 06:40–09:15 |
| 6 | Martina García Rios | Undo, Restart, snapshots y puntaje | 09:15–13:00 |
| 7 | Giuliano Calicchia | Interfaz y Abstract Factory | 13:00–16:45 |
| 8 | Lucas Capatti | Command y Replay | 16:45–18:25 |
| 9 | Lucas Capatti | Cierre y decisiones de diseño | 18:25–18:45 |

Distribución: 3:45 minutos por integrante (18:45 en total). Lucas interviene al inicio y al cierre.

## 2. Introducción, MVC y carga TXT — Lucas (diapositivas 1–2)

Sokoban es un juego de rompecabezas 2D desarrollado en Java con Swing. Su objetivo es empujar cajas hasta los destinos; además incorpora hielo, cajas frágiles y llave, cerrojos, muros dinámicos, Undo, Restart y puntuación. Sus dos funcionalidades adicionales son el cambio de familias visuales y Replay.

**MVC.** La necesidad es separar las reglas de movimiento y el estado de la interfaz gráfica. El modelo conserva el mundo, aplica las reglas y calcula el resultado; el controlador coordina las acciones del jugador; la vista representa el resultado. Esto permite probar las reglas sin abrir ventanas y cambiar la apariencia sin modificar el motor. `GameController` coordina; `GameSession` administra el intento; `RuntimeMovementEngine` resuelve la transición sobre `RuntimeState`; `GameView` define la frontera hacia Swing.

**Construcción de niveles.** Los TXT permiten incorporar distintas configuraciones de tablero sin cambiar el código. `LevelLoader` realiza la lectura y los parsers interpretan y validan el formato. `LevelBuilder` reúne filas de manera incremental y publica una definición `Level`/`Board` válida. Es una aplicación simplificada de la intención de Builder, no una estructura GoF completa con Director y múltiples constructores. Una carga inválida conserva la sesión anterior. Los datos especiales se conservan en las definiciones correspondientes; la entrada válida no garantiza que cualquier puzzle tenga solución.

**Demo de referencia:** menú, selector ABRIR NIVEL TXT, `demo.txt`, iniciar la campaña y completar el primer nivel (`→ → →`, 993 puntos previstos sin Undo).

## 3. Cerrojos, victoria y Observer — Juan (diapositiva 3)

Cuando una caja llave ocupa un cerrojo, el modelo determina la apertura de los muros asociados. Sólo una caja llave activa el cerrojo; un muro se abre cuando al menos uno de sus cerrojos asociados está activo. Retirar la llave puede cerrar el muro, pero se rechaza una transición que lo cerraría sobre una celda ocupada. El cerrojo no equivale a un destino.

El problema de diseño es comunicar el cambio a distintos componentes sin que el motor necesite conocer la interfaz o el sistema de audio. **Observer** es un patrón de comportamiento que establece una relación de uno a muchos: un sujeto mantiene observadores y los notifica ante cambios relevantes. En nuestra implementación, `GameController` publica los estados confirmados mediante `LockChangeSubject`; sus observadores permiten que la vista informe el cambio y que se soliciten los efectos sonoros. El contrato es `Observer.onLockChange(Event)`. El sujeto permite suscripción y desuscripción y compara los estados anterior y posterior. Un error de tipo `RuntimeException` de un suscriptor no revierte una transición ya confirmada.

La separación clave es que el modelo **decide** y Observer **notifica**. La vista recibe el nuevo estado por MVC y los observadores reaccionan a eventos confirmados. Esto reduce el acoplamiento y protege la autoridad del modelo. No se delegan las reglas de apertura y cierre a Swing ni al audio.

`VictoryEvaluator` verifica que exista al menos una caja y un destino, que las cantidades coincidan y que todos los destinos estén cubiertos. `LevelProgression` conserva el resultado, finaliza el nivel y habilita el avance cuando corresponde.

**Demo de referencia:** `campaign-b`, primer empuje de caja llave; explicar el muro y los avisos; completar el recorrido hasta victoria (983 puntos previstos sin Undo).

## 4. Motor e inmutabilidad — Santino (diapositivas 4–5)

El modelo dispone de una única representación autoritativa del mundo. Cada movimiento válido genera un nuevo `RuntimeState` en lugar de modificar el anterior; si el movimiento está bloqueado, el motor conserva el estado y no aumenta las métricas. La sesión mantiene la referencia al estado actual. Inmutabilidad no significa que todos los objetos de la aplicación sean inmutables.

Esta decisión permite conservar estados anteriores sin que las transiciones posteriores los alteren accidentalmente. Facilita Undo y Replay y simplifica la separación entre reglas y animación. Tiene costes de copia y validación; no se afirma un ahorro de rendimiento cuantificado.

El motor concentra las colisiones, empujes, deslizamientos y resistencia. Una caja sobre hielo continúa hasta salir de la superficie o encontrar un obstáculo; el jugador no se desliza. Aunque recorra varias celdas, se contabiliza un movimiento y un empuje. Una caja frágil pierde una unidad de resistencia por empuje; al agotarse se rompe. Si el empuje incluye hielo, primero se calcula la trayectoria y luego se resuelve la rotura. No es necesario un choque para romperla.

**Justificación:** GRASP Alta cohesión y Bajo acoplamiento permiten separar las reglas de la presentación; Experto en Información asigna las consultas del mundo a `RuntimeState`. SRP separa motivos de cambio: el motor decide la transición y el panel la representa. `RuntimeMoveResult` comunica la trayectoria y la rotura; `BoardPanel` y `BoxAnimation` las representan sin recalcular la física. No hay un solucionador general de niveles.

**Demo de referencia:** `ice_demo` (`↓ →`), seguido de `fragile_demo` (`→ → →`), con 996 y 991 puntos previstos sin Undo.

## 5. Undo, Restart, Memento y puntaje — Martina (diapositiva 6)

El problema de Undo es recuperar una situación previa incluso cuando una caja cambia de resistencia, se desliza o desaparece. Invertir manualmente un movimiento podría no recuperar toda la información perdida. Por eso la solución utiliza snapshots privados de estado y métricas reversibles, una adaptación de la intención de **Memento**, patrón de comportamiento que permite guardar y restaurar estados sin romper el encapsulamiento.

`GameSession` conserva hasta quince snapshots correspondientes a movimientos exitosos. Cada Undo recupera exactamente cinco movimientos, con un máximo de tres usos por intento; volver a moverse no recupera ese presupuesto. La sesión administra sus propias instantáneas, sin Caretaker independiente: no se presenta como una implementación GoF completa con sus tres roles separados. El mundo, los movimientos y los empujes se restauran; el total de usos de Undo permanece registrado. Restart inicia otro intento, recupera el estado inicial y reinicia historial y métricas.

`ScoreCalculator` concentra el cálculo y `LevelResult` representa el resumen de la sesión. La fórmula documentada es `max(0, 1000 - movimientos - 2*empujes - 10*usosUndo)`. Los usos de Undo se penalizan con diez puntos, aunque al restaurar movimientos y empujes la puntuación puede subir respecto al momento anterior al deshacer. Separar el cálculo del panel evita que la vista administre reglas de puntuación.

**Demo de referencia:** nivel `fragile-ice-demo`, reinicio, cinco movimientos exitosos y Undo, repetición de la solución hasta victoria. El puntaje previsto para siete movimientos, dos empujes y un Undo es 979.

## 6. Interfaz y Abstract Factory — Giuliano (diapositiva 7)

La primera funcionalidad adicional permite cambiar el estilo visual completo sin modificar la partida. El problema no es sólo elegir colores aislados, sino obtener productos visuales que pertenezcan a una misma familia.

**Abstract Factory** es un patrón creacional que permite obtener familias de productos relacionados sin depender de sus implementaciones concretas. En nuestro juego `VisualThemeFactory` ofrece `UiPalette` (paleta) y `SpritePresentation` (transformación de imágenes). Las familias concretas Original Cobre, Nocturno y Jade proporcionan productos compatibles y la presentación consume la fábrica seleccionada. `VisualThemeController`, `VisualThemeApplier`, `BoardPanel` y `PixelAssets` coordinan y aplican la familia visual. Los temas alternativos mezclan 55 % de color original y 45 % de tinte, preservando la transparencia. No se sobrescriben los sprites almacenados.

Esta organización concentra la variación de la estética, evita distribuir la lógica de selección por el motor y favorece OCP: añadir una familia exige su implementación y registro, pero no cambiar las reglas. Abstract Factory no construye niveles. Builder ensambla progresivamente un objeto; Abstract Factory provee familias de productos.

**Demo de referencia:** opciones, recorrer Original Cobre, Nocturno y Jade; regresar mediante CONTINUAR y preservar estado y métricas.

## 7. Command y Replay — Lucas (diapositiva 8)

Para nuestra segunda funcionalidad adicional utilizamos **Command**, un patrón de comportamiento que consiste en encapsular una acción dentro de un objeto, permitiendo almacenarla y ejecutarla posteriormente.

En lugar de guardar una imagen o una copia del tablero por cada movimiento, guardamos las acciones necesarias para reconstruir la partida desde su estado inicial. Cada movimiento exitoso se representa mediante un comando que conoce la dirección y solicita al motor que lo ejecute. Los comandos pueden ejecutarse individualmente o de forma secuencial, utilizando las mismas reglas que en la partida original.

La reproducción trabaja sobre un estado independiente: podemos pausarla, reiniciarla o regresar a la pantalla de victoria sin modificar el progreso ni el puntaje. Mientras que Undo recupera estados anteriores, Replay vuelve a ejecutar las acciones que permitieron llegar al resultado.

| Participante | Código | Responsabilidad |
|---|---|---|
| Comando concreto | `MoveCommand` | Encapsula la dirección y delega la ejecución al motor. |
| Receptor | `RuntimeMovementEngine` | Aplica las reglas al estado recibido. |
| Invocador | `ReplayPlayer` | Ejecuta los comandos sobre un estado propio. |
| Cliente/coordinación | `GameController`, `ReplayRecorder` | Coordinan la creación, el registro y la preparación de la reproducción. |
| Traza | `ReplayTrace` | Conserva estado inicial, secuencia y datos necesarios para verificar el resultado. |

La implementación es una variante simplificada porque sólo necesitamos un tipo de acción parametrizada por dirección, no una jerarquía de comandos concretos. En el primer nivel se registran tres comandos hacia la derecha; al ejecutarlos en orden, el motor reconstruye el tablero final en Replay sin modificar la sesión original.

Cuando Undo tiene éxito se eliminan los cinco comandos deshechos; Restart inicia un registro nuevo. Los intentos bloqueados no se registran. Replay representa la secuencia efectiva del intento final, no todas las pulsaciones ni todos los intentos. Antes de habilitarlo, `ReplayRecorder.verified` comprueba que pueda reconstruirse el estado final y las métricas de movimientos y empujes. Una traza inválida o que supere el límite de 10.000 comandos deshabilita Replay sin anular la victoria; no se trata de un sistema antitrampas.

**Justificación:** elegimos Command porque necesitábamos convertir acciones de ejecución inmediata en objetos almacenables y ejecutables posteriormente, reutilizando el motor y aislando la reproducción de la partida activa.

**Demo de referencia:** VER REPLAY, STEP, RESET, PLAY, PAUSE y BACK; comprobar que las métricas de victoria se conservan.

## 8. Cierre y síntesis — Lucas (diapositiva 9)

| Concepto | Decisión de diseño |
|---|---|
| MVC | Separar modelo, coordinación y presentación. |
| Builder (simplificado) | Construir y validar niveles incrementalmente. |
| Inmutabilidad, SRP y GRASP | Conservar una autoridad del estado y concentrar reglas. |
| Observer | Notificar cambios a los suscriptores sin delegar las reglas del mundo. |
| Memento (adaptación con snapshots) | Guardar y restaurar estados para Undo. |
| Abstract Factory | Proporcionar familias visuales compatibles. |
| Command | Encapsular y registrar acciones para Replay. |

Los tres patrones GoF centrales son Abstract Factory, Command y Observer. La carga incremental y el historial se relacionan con las intenciones de Builder y Memento mediante implementaciones simplificadas. Los patrones no son un fin en sí mismos: responden a necesidades concretas del videojuego.

## 9. Materiales y referencias

- [Presentación de la defensa](Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx) y [PDF](Los_Force_Push_Sokoban_Defensa_18-11-2026.pdf).
- [Informe técnico](INFORME_TECNICO.pdf), [modelo StarUML](Los_Force_Push_Sokoban_ENTREGA_FINAL.mdj) y [video de gameplay](VIDEO_GAMEPLAY.mp4).
- [Arquitectura](../docs/architecture/ARCHITECTURE.md), [decisiones](../docs/decisions/DECISION_LOG.md), [responsabilidades](../docs/architecture/RESPONSIBILITIES.md), [formato TXT](../docs/requirements/TXT_FORMAT.md) y [pruebas](../docs/testing/TEST_STRATEGY.md).
- [Guía de ejecución y demostración](../docs/demo/DEFENSA_OPERACION.md), [índice de entregables](README.md) y [manifiesto SHA-256](SHA256SUMS.txt).
