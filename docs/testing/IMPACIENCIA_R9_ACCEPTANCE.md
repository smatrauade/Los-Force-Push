# Aceptación experimental R9: campaña y evidencia

La campaña se ejecuta desde **NUEVA PARTIDA / DEMO**; los TXT son recursos externos versionados, cargados por el controlador real. Las decisiones de esta rama son individuales y no constituyen aceptación del equipo. El commit final se obtiene con `git rev-parse HEAD`; la ejecución descrita probó el worktree R9 basado en `dce16407eae3f8d88c63b64e04878ac2958884bf`.

| Archivo | Formato | Testigo | Movimientos / empujes / score | Resultado esperado y observado |
|---|---|---|---|---|
| [campaign-a.txt](../../src/desktop/resources/levels/campaign-a.txt) | STANDARD | RIGHT | 1 / 1 / 9965 | Caja normal al Goal, victoria y avance |
| [campaign-b.txt](../../src/desktop/resources/levels/campaign-b.txt) | EXTENDED | RIGHT LEFT DOWN DOWN RIGHT RIGHT UP RIGHT UP LEFT LEFT | 11 / 3 / 9815 | KeyBox activa Lock y abre muro; luego llega a Goal, victoria y avance |
| [campaign-c.txt](../../src/desktop/resources/levels/campaign-c.txt) | EXTENDED | RIGHT RIGHT | 2 / 1 / 9955 | Fragile resistencia 2 desliza dos celdas de hielo hasta Goal con resistencia 1; victoria y fin de campaña |

`CampaignAcceptanceTest` carga los archivos reales dos veces, comprueba cada movimiento, score, no duplicación, conservación de instantáneas y equivalencia completa del replay. También verifica Undo y Restart en el nivel 3, replay de la trayectoria posterior y score 9855 con una penalización Undo acumulada. Ningún testigo comienza ganado. Los dos primeros archivos se comparan contra los fixtures originales en `DesktopIntegrationTest`.

## Matriz de comportamiento

| Requisito | Comportamiento | Test o fixture | Resultado esperado y actual | Evidencia | Estado |
|---|---|---|---|---|---|
| Movimiento | Cardinal, bloqueo por muros/borde, sin arrastre ni cadena, estados inmutables | MovementTest, RuntimeMovementTest | Transiciones y bloqueos correctos | Runner core | TESTED |
| Hielo/Fragile | Deslizar hasta salida o frenar en obstáculo/borde; decrementar por empuje; E04 rompe después del deslizamiento | RuntimeMovementTest y campaign-c | Correcto; Fragile sobre Goal sobrevive con resistencia 1 | Core y CampaignAcceptanceTest | TESTED |
| Victoria | Fragile destruida no ocupa Goal; KeyBox en Lock no es Goal | CompletionTest | Sin victoria prematura | Runner core | TESTED |
| Lock y muros | Activación, tránsito, cierre ocupado atómico, múltiples Lock política OR | DynamicWorldTest | Estado derivado coherente, transición fallida atómica | Runner core | TESTED |
| Sesión | 15 instantáneas, Undo 5/3, rechazo, Restart y métricas acumulativas | GameSessionTest, CampaignAcceptanceTest | Restauración íntegra y presupuesto previsto | Core y desktop | TESTED |
| Progresión y score | Finalizar una vez, cargar siguiente, fallo de carga sin pérdida, completar campaña | CompletionTest, CampaignAcceptanceTest, DesktopUiSmokeTest | Puntuación y avance reproducibles | Core, headless y ventana | TESTED |
| TXT | STANDARD/EXTENDED, UTF-8, tamaño, IDs, asociaciones y geometría inválidos | LevelLoadingTest, C2LevelLoadingTest, ExperimentalFragileParserTest, ExtendedLevelTest | Rechazos tipados, sin publicación parcial | Entry points core | TESTED |
| Carga en sesión | Archivo ausente/UTF-8 inválido, ruta con espacios; fallo antes y después de victoria | CampaignAcceptanceTest, DesktopIntegrationTest | Sin pérdida de partida ni resultado | Desktop headless | TESTED |
| Interfaz | HUD, pausa, bindings, temas, victoria, avance, replay y fin | DesktopUiSmokeTest | Tres niveles completados desde ventana; acciones invocadas programáticamente | Smoke X11 | TESTED |
| Replay y Observer | Confirmados, Undo/Restart, límite 10000, baja y eventos efectivos | DesktopExtensionsTest, CampaignAcceptanceTest | Replay íntegro; sin duplicación de eventos observada | Desktop headless | TESTED |
| Tamaño y recursos | Archivo cercano al límite, grilla grande, movimientos/replay y temas repetidos | CampaignAcceptanceTest | Ejecución acotada sin fallos | Desktop headless | TESTED |
| Foco/teclado real | Robot envía RIGHT a ventana | Intento Robot | `keyPress` quedó bloqueado en portal gráfico `ScreencastHelper.remoteDesktopKeyImpl`; proceso de prueba interrumpido | Traza JVM; smoke usa acciones, no Robot | NOT_RUN |
| Diálogo de selección | Interacción humana con JFileChooser | Guía manual | Formatos cargados por controlador; diálogo no ejercitado físicamente | Sin observador humano | NOT_RUN |
| Legibilidad y sonido | Inspección visual humana y escucha | Guía manual | Pendiente observador | No se infiere de WAV o dispatch | NOT_RUN |
| Windows/macOS | Ejecución nativa | Runners versionados | Pendiente plataformas | Revisión PowerShell no es ejecución | NOT_RUN |

Runners Linux con JDK 17 en PATH: `bash scripts/test-level-loading.sh --movement`, `bash scripts/run-swing.sh --test`, `bash scripts/run-swing.sh --ui-test` y `bash scripts/verify-repo-readonly.sh`. C2 y Fragile requieren sus entrypoints explícitos en [TEST_STRATEGY](TEST_STRATEGY.md). Compilación estricta `--release 17 -encoding UTF-8 -Xlint:all -Werror`, core limitado a `java.base`, desktop a `java.base,java.desktop`. El smoke gráfico requiere display accesible. R9 se ejecutó en Ubuntu Linux, OpenJDK 17.0.20.1; las salidas y límites se registran también en la estrategia.

## QA manual pendiente (10–15 minutos)

1. Ejecutar `bash scripts/run-swing.sh`; iniciar demo. Mover con flechas y comprobar que el HUD cambia y las paredes bloquean. Pausar con Esc y volver.
2. Completar nivel 1 empujando a la derecha. En victoria comprobar 1 movimiento, 1 empuje, 9965; reproducir, pausar, reiniciar replay y volver al resultado. Avanzar.
3. En nivel 2 mover al menos cinco veces, usar Z y comprobar que vuelven tablero y contadores; usar R y confirmar reinicio. Cambiar tema en Opciones y comprobar lectura, sprites, zoom y estado conservado. Resolver con el testigo, avanzar.
4. En nivel 3 mover RIGHT RIGHT; observar deslizamiento de Fragile al destino y victoria. Probar otra partida, romperla si se desea y verificar Restart; confirmar que el nivel se ve completo o desplaza correctamente.
5. Abrir un TXT STANDARD y uno EXTENDED mediante sus opciones; probar archivo inválido y confirmar que la partida anterior permanece. Verificar silencio con M y escuchar efectos con audio habilitado. Cerrar y comprobar salida.

Registrar plataforma, persona observadora, fecha, resultados y defectos; hasta entonces la escucha y legibilidad son `NOT_RUN`. La integridad automática de assets no demuestra titularidad o licencia: conservar incertidumbres en [política de assets](../assets/ASSET_POLICY.md) y [uso de IA y licencias](../punto5/USO_IA_Y_LICENCIAS.md). Selector de temas y replay son candidatos técnicos RF-12, sin elegibilidad docente confirmada ni selección TEAM registrada para replay; OQ-10 permanece abierta.
