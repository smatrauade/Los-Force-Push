# Estado vivo — Los Force Push

AS_OF = 2026-10-28. Git/GitHub prevalece para el HEAD y los PR actuales. Este documento registra eventos y capacidades; no fija un HEAD permanente ni concede permisos de ejecución.

## Integraciones verificadas

| PR | Alcance | Commit de merge histórico |
|---|---|---|
| [#1](https://github.com/smatrauade/Los-Force-Push/pull/1) | Bootstrap | ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed |
| [#2](https://github.com/smatrauade/Los-Force-Push/pull/2) | Reconciliación | 1943053ec520ff3d8be0aecb21c2f417f3126380 |
| [#3](https://github.com/smatrauade/Los-Force-Push/pull/3) | Loading y Movement | e8f76cec23de019cd244b6d089bd6de3a24251a3 |
| [#4](https://github.com/smatrauade/Los-Force-Push/pull/4) | Aislamiento de runners | ee83f9ebd30ee1b553c555c89d9ea453131d6678 |
| [#5](https://github.com/smatrauade/Los-Force-Push/pull/5) | Swing V1 | 54c03d4520dfc542c125686e5446687dc07d85db |
| [#6](https://github.com/smatrauade/Los-Force-Push/pull/6) | Rehidratación | 2c2c2e4c6180ca0639e511c480ca5b93cd4e4a01 |
| [#7](https://github.com/smatrauade/Los-Force-Push/pull/7) | Decisiones TEAM | 7d3a30f04285d526709bbbf91e8725c61a187aac |
| [#9](https://github.com/smatrauade/Los-Force-Push/pull/9) | Swing pixel art / Punto 5 | fafae98642cefd38e466c689b3c8a16825773738 |
| [#8](https://github.com/smatrauade/Los-Force-Push/pull/8) | TXT C2-L7 | 565376452affa9b2146569e5da27dbfd1ee9d4f0 |
| [#10](https://github.com/smatrauade/Los-Force-Push/pull/10) | Continuidad y verificador | c6eb755388da142b6c8424d2d46aec2dc03ce622 |

PR #8 y #9 están MERGED. El merge de #8 tiene padres fafae98642cefd38e466c689b3c8a16825773738 y 289fef6eda97a2bbd5593c74f06382ae6caf433c; conserva sus dos commits y el árbol 1796932307df319e3636daee942cf49920b4eb05. Las ramas remotas de ambos incrementos se retiraron tras verificar integración y consumidores. Sus commits permanecen en main. Al corte posterior al merge de #8 no había otros PR abiertos; consultar GitHub para cambios posteriores y para el estado del mantenimiento documental.

## Software que existe

- [LevelLoader / LevelParser / C2LevelParser](../../src/main/java/ar/edu/uade/sokoban/level): lectura externa UTF-8 estricta, máximo 65.536 bytes, legacy y C2 opt-in; Board inmutable, grillas hasta 100×100, exactamente un jugador.
- [MovementEngine](../../src/main/java/ar/edu/uade/sokoban/movement/MovementEngine.java): movimiento cardinal y empuje de una caja normal. MOVED/PUSHED producen Board nuevo; BLOCKED conserva la misma instancia.
- [Desktop](../../src/desktop/java/ar/edu/uade/sokoban/desktop): menú, juego, HUD, pausa, opciones, ayuda, zoom, sprites y audio. GameController coordina carga/movimiento; BoardPanel sólo representa; PixelAssets y SoundManager cargan desde classpath.
- [UiCommands](../../src/desktop/java/ar/edu/uade/sokoban/desktop/UiCommands.java) expone callbacks opcionales y [LevelResult](../../src/desktop/java/ar/edu/uade/sokoban/desktop/LevelResult.java) datos finales. No implementan Undo, Restart, persistencia, score ni progresión.
- [Recursos](../../src/desktop/resources/assets): 31 PNG, incluidos 29 base y dos variantes opcionales; 12 WAV. [Manifiesto](../punto5/ASSETS_REGISTRO_SHA256.csv), [contrato entre módulos](../punto5/INTEGRACION_EQUIPO.md) y [derechos pendientes](../punto5/USO_IA_Y_LICENCIAS.md).

## Spike local C2-L7 — 2026-10-24

Ese título conserva el ancla histórica; el incremento ya está integrado por PR #8. C2LevelParser es un helper interno sin estado compartido. Header exacto !sokoban-txt 2, [grid] y [attributes] obligatoria vacía; siete aliases y siete formas largas con igual Board. No hay fallback legacy para un encabezado reservado inválido. Atributos y entidades especiales se rechazan antes de publicar Level.

C2 sigue EXPERIMENTAL_NOT_RATIFIED; su integración no ratifica la gramática completa ni cierra OQ-06/07/08/11. No contiene resistencia individual ni asociaciones Lock↔Walls. [Comandos y evidencia](../testing/TEST_STRATEGY.md#spike-experimental-c2-l7--2026-10-24).

## Evidencia y alcance

La auditoría SOKOBAN_POST_PR9_INTEGRATION_R1 del 28/10 ejecutó sobre el árbol 1796932307df319e3636daee942cf49920b4eb05: Loading 40, Movement 31, C2 32, Desktop 16, VisualAssets 44, UI Smoke nativo automatizado 17 y CLI PS5.1/7 18, todos PASS/exit 0. Windows, Temurin 21.0.12.1, release 17, UTF-8 y compilación estricta. También verificó 43 hashes y exportación limpia; Desktop/Assets pasaron con TXT C2 equivalente al demo. Son resultados de esa misión anterior, no una nueva ejecución funcional en este mantenimiento.

[TEST_STRATEGY](../testing/TEST_STRATEGY.md) distingue esa evidencia de las pruebas actuales del verificador y de los resultados históricos. No se afirma QA manual nueva, escucha de altavoces, GUI Linux ni macOS. Los logs/capturas originales no se incorporan a Git.

## Checkpoint mayoritario del equipo — 2026-10-23

Autoridad canónica: [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23). Deuda residual: [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md). GAME_RULE_DECISIONS = MAJORITY_ACCEPTED_PARTIAL; TEAM_ACCEPTANCE = NOT_RECORDED para el código. E04/E08 requieren revisión de compatibilidad normativa. Patrones y UML no se ratifican mediante merges o PASS.

## Estado posterior al merge de PR #5

El merge de #5 y su QA previa son historia: [checkpoint Windows](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Probó el código 0670a4d5137f0e9f1db221866a02c29987024847; el commit documental posterior a722d4e5229e4151e9ca73971e1987e05bc1dac7 no es otro test. Sus estados Draft/premerge no describen el PR actual.

## Estado comprobado y cómo continuar

[README](../../README.md) contiene ejecución desde clon; [TEST_STRATEGY](../testing/TEST_STRATEGY.md), suites y regresión del verificador. No se requieren ZIP de instalación ni assets externos. Java/build definitivo sigue OQ-11 OPEN; los runners provisionales compilan a release 17. [Arquitectura](../architecture/ARCHITECTURE.md) y [UML](../architecture/uml/STARUML_STATUS.md) separan implementación de candidato.

Próximo dominio TXT/niveles: revisión del contrato experimental de atributos individuales y especificación de su integración futura, respetando E01/E02 y la compatibilidad legacy/C2. Requiere especificación y autorización propias; no habilitar especiales por inferencia. Siguen pendientes hielo, Fragile/KeyBox/Lock, Undo/Restart, score, victoria/progresión, persistencia y segunda adicional. [TODO](../../TODO.md) reúne tareas funcionales y entregables.

## Historia y fuentes

Los informes [Loading](../experiments/LEVEL_LOADING_R1.md), [Movement](../experiments/MOVEMENT_R1.md) y [Swing V1](../experiments/SWING_VERTICAL_SLICE_V1.md) conservan evidencias fechadas. [HISTORICAL_EVIDENCE](../testing/HISTORICAL_EVIDENCE.md) describe A/B sin fuentes recuperadas, no mecánicas disponibles hoy. Cápsulas y checkpoints permanecen inmutables. [SOURCE_REGISTRY](SOURCE_REGISTRY.md) distingue bytes presentes, fuentes externas y transcripciones TEAM; no atribuir lectura directa de originales académicos ausentes.

## Prototipo experimental de atributos individuales

ExperimentalFragileParser y ExperimentalFragileLevel son un spike no ratificado en el código de este snapshot; consultar Git/PR para el estado de integración. La plantilla espacial deja vacías las celdas frágiles y la lista inmutable conserva ID, posición y resistencia, sin crear un Level jugable con una caja normal sustituta. El agregado valida IDs y posiciones únicos, límites del tablero, piso/destino vacío, no nulos y resistencia positiva; preserva Board y lista inmutables. Loader/Parser públicos siguen rechazando el mismo TXT. [Contrato y límites](../../README.md#prototipo-experimental-de-atributos-individuales); [pruebas](../testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales). OQ-06/OQ-08 no se cierran.
