# Decisiones del diseño implementado

Este documento explica las decisiones de la versión entregada a partir de sus clases y contratos. Las justificaciones describen el diseño existente; no registran nuevas votaciones ni alteran las reglas del programa.

| Problema | Elección | Alternativa y motivo | Consecuencia verificable |
|---|---|---|---|
| Probar reglas sin abrir Swing | Modelo separado y frontera `GameView` | Poner movimiento en listeners acoplaría reglas a ventanas | Tests core sin `java.desktop`; sondas de controlador sin JFrame |
| Mantener una sola verdad espacial | `RuntimeState` inmutable y `Board` como proyección | Dos grillas mutables exigirían sincronización | Motor y sesión son autoridad; vista/animación solo proyectan |
| Publicar niveles completos | Lectura acotada, parsers estrictos y `LevelBuilder` concreto | Construcción directa sería válida, pero dispersaría acumulación/validación | Entrada inválida no reemplaza sesión; sin Director ni fábrica abstracta para el loader |
| Preservar atributos de cajas | Identidad y resistencia por ID en la definición Fragile | Posición como identidad cambiaría al mover; un valor global perdería individualidad | `fromExperimentalFragile` consume plantilla y metadatos |
| Deshacer transiciones complejas | Snapshots acotados en `GameSession` | Invertir un movimiento no reconstruye fácilmente fragilidad, hielo y cerrojos | Undo restaura mundo y métricas reversibles; usos permanecen acumulados |
| Cambiar todo el aspecto | Abstract Factory para paleta y sprites | Condiciones por tema en cada widget dispersarían reglas visuales | Tres familias; vuelta al original y alfa cubiertos por `VisualThemeTest` |
| Repetir una partida sin cambiarla | Command y estado propio de Replay | Reutilizar sesión alteraría historial/score; snapshots duplicarían almacenamiento | Comandos se verifican y ejecutan en otro mundo; límite de 10.000 |
| Avisar cambios de cerrojos | Observer de hechos confirmados | Acoplar el motor a avisos/sonido impediría pruebas independientes | Suscriptores aislados; muros siguen derivados en el modelo |
| Puntuar con una sola fórmula | `ScoreCalculator` puro | Strategy no aporta variación mientras existe una política única | Saturación en cero y métricas de Undo explícitas |
| Gestionar audio | `SoundManager` por ventana | Singleton introduciría estado global y dificultaría aislamiento | Clips se cierran con la ventana; ausencia de dispositivo no bloquea reglas |
| Mostrar deslizamientos | Trayectoria emitida por el motor | Recalcular física en el panel duplicaría reglas | Animación cancelable y victoria después del recorrido visual |

## Reglas y límites de las elecciones

El formato usa una grilla de tokens combinados, con atributos por ID para cajas frágiles y asociaciones de muros. La sintaxis soportada se documenta en [TXT](../requirements/TXT_FORMAT.md); una entrada válida no garantiza que cualquier puzzle tenga solución. Los cinco niveles distribuidos tienen recorridos de solución cubiertos por pruebas.

En hielo, el motor completa el desplazamiento posible y después resuelve la rotura de Fragile si agotó su resistencia. Una caja llave sobre Lock abre muros pero no completa un Goal; retirar la llave puede cerrar el muro, y se rechaza atómicamente el cierre sobre una celda ocupada. Con múltiples cerrojos asociados, basta uno activo para abrir.

Undo requiere cinco movimientos exitosos y no recupera usos al mover de nuevo. Restart y cambio de nivel inician otro intento; los resultados ya terminados permanecen en la progresión. La fórmula resta movimientos, empujes y total de Undo, sin penalización extra por Restart. Son los comportamientos concretos de sesión/motor, no una atribución de aprobación docente particular.

## Costes asumidos

La inmutabilidad copia datos del mundo; Replay usa memoria y validación lineales sobre la secuencia. La lectura local se ejecuta en EDT y puede demorar en filesystems lentos. Hay un solo comando reproducible y una sola política de score, por lo que no se agregan interfaces o patrones para variaciones inexistentes.

Undo usa snapshots internos, pero no se presenta como un Memento GoF completo. Command corresponde a Replay. Observer desacopla avisos y audio, sin decidir muros. No se atribuyen Strategy, Singleton, Factory Method o Facade por analogía de nombres.

Véanse [responsabilidades y principios](../architecture/RESPONSIBILITIES.md), [diagramas](../architecture/uml/pr17-rc-ffa27f9/README.md) y [pruebas](../testing/TEST_STRATEGY.md).
