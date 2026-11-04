# Contrato de integración entre presentación y dominio

`LAST_REVIEWED=2026-11-04` · `BRANCH_SCOPE=PR17_DRAFT`. Este contrato vivo refleja el candidato integrado; los cortes Punto 5 previos se conservan en Git. No acredita incorporación a `main`, revisión TEAM ni derechos.

## Autoridad y flujo

`RuntimeState` es el mundo inmutable; `RuntimeMovementEngine` resuelve transiciones. `GameSession` concentra historial/Undo/Restart/métricas; `LevelProgression` finaliza y avanza. `GameController` coordina carga, publicación, registro Replay y eventos; `GameView`, `SwingApplication` y `BoardPanel` presentan. `ScoreCalculator` aplica la fórmula TEAM; `LevelResult` transporta el resultado ya calculado.

| Frontera | Contrato efectivo | Límite |
|---|---|---|
| `GameView.render(GamePresentation)` | Comparte RuntimeState, nombre y métricas del intento | Board es proyección; no otra autoridad |
| `GameController.load/loadCatalog` | Construir nivel completo antes de reemplazar; error conserva sesión/traza | E/S local limitada, síncrona en EDT |
| `UiCommands` | undo/restart/nextLevel según disponibilidad; null deshabilita | save/loadSaved siguen null; callbacks no son Command GoF |
| `animateBox(BoxAnimation)` | Trayectoria/identidad/resistencia/rotura desde RuntimeMoveResult | No recalcular física ni inferir resistencia desde un PNG |
| `showVictory(LevelResult)` | Finalización de sesión, incluida carga ya resuelta | VictoryGate difiere sólo FINISH mientras anima |
| `configureReplay(ReplayTrace)` | Traza completa verificada del nivel actual, null la retira | Sólo se conserva al completar; fallo de carga no la borra |
| `onLockChange(Event)` | Observer de hechos confirmados para avisos/SFX | No controla muros; cierre/Undo derivados por RuntimeState |

## TXT y runtime

Los archivos se leen UTF-8 estricto y hasta 65.536 bytes. `LevelLoader.load` acepta legacy/C2 ordinario e ICE, rechaza Fragile para evitar perder IDs. `loadExperimentalFragile` devuelve plantilla y lista de ID/posición/resistencia; `RuntimeState.fromExperimentalFragile` usa ambas partes. `loadExtended` carga KeyBox/Lock/Wall con asociaciones explícitas; `fromExtended` crea runtime. El controlador identifica la ruta por validación estricta, no por nombre de archivo. [Contrato soportado D-020](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md) y [límites de las extensiones implementadas](../integration/PR17_FUNCTIONAL_RC_2026-11-04.md).

No enviar la plantilla Fragile sola a Movement ni convertirla en caja normal. Los parsers validan estructura, no garantizan resolubilidad de cualquier TXT. Los cinco niveles distribuidos tienen testigos ejecutados usando el mismo catálogo que el menú.

## Presentación y ciclo de vida

`BoardPanel` dibuja terreno→objetivo→ocupantes y proyecta el path/rotura sin caja fantasma. `showState`, Undo, Restart, carga exitosa, pausa/navegación y cierre cancelan timers. La carga inválida durante victoria animada conserva y muestra el resultado final. Flechas sólo actúan en GAME y cuando no hay animación. `Z`/`R` usan los mismos callbacks que botones.

Replay tiene otro BoardPanel y ReplayPlayer; nunca recibe GameSession ni modifica score/progresión. Su Timer EDT invalida callbacks al pausar/resetear/navegar/cerrar; Step es discreto, sin segunda animación ICE ni SFX. El mismo `VisualThemeController` aplica Abstract Factory a tablero real, Replay, menú y árbol Swing; zoom al abrir Replay conserva la escala del tablero real.

`LockChangeSubject` se crea por controlador/ventana. Dos suscriptores efectivos presentan cambios y reproducen LOCK/GATE para MOVE; Undo/Restart conservan sus SFX propios. Los eventos son inmutables, deduplicados y posteriores a publicación; close retira suscriptores. Las paredes no son listeners ni tienen estado mutable paralelo.

## Recursos y audio

PixelAssets lee/cacha 31 PNG 32×32 del classpath; los temas alternativos transforman en memoria, con alfa preservado. SoundManager posee clips por ventana, mute y degradación controlada sin dispositivo; no es Singleton. No se emiten SFX desde paintComponent. El [manifiesto](ASSETS_REGISTRO_SHA256.csv) conserva 43 hashes; [derechos e IA](USO_IA_Y_LICENCIAS.md) siguen pendientes de evidencia del equipo. Python/Pillow no son dependencias de ejecución.

## Pruebas y defensa

[TEST_STRATEGY](../testing/TEST_STRATEGY.md) y [auditoría RC](../integration/PR17_FUNCTIONAL_RC_2026-11-04.md) distinguen headless, ventana nativa automatizada, audio por dispositivo y escucha humana. [Guía de clon](GUIA_INSTALACION_GITHUB_DESKTOP.md) y [README actual](../../README.md) contienen ejecución. Los roles reales permiten defender MVC/GRASP, Abstract Factory, Command y Observer; elegibilidad académica de las dos adicionales sigue OQ-10 OPEN.
