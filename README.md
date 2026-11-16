# Los Force Push — Sokoban

Trabajo Integrador de **Procesos de Desarrollo de Software**, Ingeniería en Informática, UADE. Docente: **Jonathan Leonel Pepe**.

Sokoban 2D desarrollado en Java y Swing. Incluye cinco niveles TXT, cajas normales, frágiles y llave, hielo, cerrojos, muros dinámicos, Undo, Restart, victoria y puntuación. Las dos funcionalidades adicionales son el selector de temas visuales y la reproducción de partidas mediante Replay.

| Integrante | Legajo |
|---|---|
| Juan Ignacio Antonini | 1155532 |
| Lucas Capatti | 1109648 |
| Santino Matra | 1190926 |
| Martina García Rios | 1128267 |
| Giuliano Calicchia | 1187567 |

## Requisitos

- Un **JDK completo 17 o posterior**, con `java` y `javac` de la misma instalación. Un JRE solo no alcanza para compilar.
- Windows con PowerShell 5.1 o 7, o Linux con Bash, `find`, `sort` y `mktemp`; escritorio gráfico para abrir la ventana.
- Git para descargar por clonación. Swing está incluido en el JDK. No se necesitan Maven, Gradle ni bibliotecas externas; niveles, sprites y sonidos están incluidos.

## Descarga y ejecución

```text
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
java -version
javac -version
```

Desde la raíz del clon, en Windows:

```powershell
powershell -NoProfile -File .\scripts\run-swing.ps1
```

Con PowerShell 7 también se puede usar `pwsh -NoProfile -File .\scripts\run-swing.ps1`.

Si falta `javac`, aparece una versión anterior a 17 o `JAVA_HOME` apunta a un JRE, seleccioná un JDK completo ya instalado para esta sesión. Reemplazá la ruta del ejemplo por la de tu JDK:

```powershell
$env:SPIKE_JDK_HOME = 'C:\ruta\a\tu-jdk'
& "$env:SPIKE_JDK_HOME\bin\java.exe" -version
& "$env:SPIKE_JDK_HOME\bin\javac.exe" -version
powershell -NoProfile -File .\scripts\run-swing.ps1
```

El runner busca primero `SPIKE_JDK_HOME`, luego `JAVA_HOME` y por último PATH. No instala herramientas ni modifica la configuración global. Compila en una carpeta temporal y la elimina al cerrar el juego.

En Linux, usá una ruta de clon **sin espacios** y colocá el JDK 17+ en PATH:

```bash
bash scripts/run-swing.sh
```

El runner Bash utiliza un archivo de argumentos de compilación que requiere esa restricción de ruta. Las instrucciones y el formato TXT se amplían en la [documentación](docs/README.md).

## Cómo jugar

Elegí **NUEVA PARTIDA / DEMO**. La campaña recorre `demo`, `campaign-b`, `ice_demo`, `fragile_demo` y `fragile-ice-demo`. Empujá las cajas hasta los destinos. La caja llave activa cerrojos, pero un cerrojo no reemplaza un destino de victoria. Esperá a que termine cada animación antes de volver a mover.

| Control | Acción |
|---|---|
| Flechas | Caminar y empujar una caja; no se puede tirar ni empujar dos juntas |
| `Z` / DESHACER | Retroceder cinco movimientos exitosos; historial de 15 y hasta tres usos por intento |
| `R` / REINICIAR | Volver al estado inicial del nivel y reiniciar sus métricas |
| `Esc` | Pausa o regreso, según la pantalla |
| `M` | Activar o silenciar sonido |
| `+` / `-` | Cambiar zoom |
| OPCIONES | Elegir Original Cobre, Nocturno o Jade |

Undo requiere cinco movimientos disponibles. Moverse después de deshacer no recupera usos; Restart o cambiar de nivel inicia otro intento. El HUD muestra nivel, movimientos, empujes, usos de Undo y puntaje: `max(0, 1000 - moves - 2*pushes - 10*undoUsesTotal)`.

Tras una victoria, **SIGUIENTE NIVEL** avanza si queda otro nivel. **VER REPLAY** ofrece Play/Pause/Step/Reset/Back sobre un tablero independiente: no altera la partida, el puntaje ni el progreso. Registra los movimientos exitosos de la secuencia final tras Undo y Restart. El límite es 10.000 movimientos; si la traza no se puede validar, se conserva la victoria y no se ofrece Replay.

**ABRIR NIVEL TXT** inicia un nivel desde un archivo externo. También se puede pasar la ruta:

```powershell
powershell -NoProfile -File .\scripts\run-swing.ps1 -LevelPath 'C:\niveles\mi nivel.txt'
```

En Linux: `bash scripts/run-swing.sh '/ruta/al/nivel.txt'`. La carga inválida informa el error y conserva la partida. **GUARDAR PARTIDA / CARGAR PARTIDA están deshabilitados**: no se implementó persistencia en disco. Abrir un TXT no restaura una partida guardada. Consultá el [formato y ejemplos de niveles](docs/requirements/TXT_FORMAT.md).

## Diseño y patrones

MVC separa las reglas en el modelo, las pantallas Swing en la vista y la coordinación en `GameController`. La responsabilidad única y la alta cohesión separan lectura TXT, motor, sesión y presentación. GRASP Information Expert ubica las consultas del mundo en `RuntimeState` y las de victoria en `VictoryEvaluator`; las fronteras `GameView` y `VisualThemeFactory` reducen el acoplamiento con implementaciones concretas.

| Patrón | Clases principales | Problema que resuelve |
|---|---|---|
| Abstract Factory | `VisualThemeFactory`, `VisualThemes`, `VisualThemeController`, `VisualThemeApplier` | Cambiar juntas la paleta Swing y la presentación de sprites sin modificar reglas |
| Command | `MoveCommand`, `ReplayRecorder`, `ReplayTrace`, `ReplayPlayer` | Registrar y reproducir movimientos en un estado aislado |
| Observer | `LockChangeSubject`, `GameController`, suscriptores de interfaz y sonido | Notificar cambios confirmados de cerrojos sin acoplar el motor a Swing o audio |

Undo conserva snapshots; Replay ejecuta comandos. Los muros se calculan en el modelo y no dependen de los observadores. La [arquitectura](docs/architecture/ARCHITECTURE.md) y las [decisiones de diseño](docs/decisions/DECISION_LOG.md) explican participantes, alternativas, SOLID y GRASP.

## Estructura del proyecto

| Ruta | Contenido |
|---|---|
| `src/main/java` | Lectura TXT, mundo, movimiento, sesión, victoria y Replay |
| `src/desktop/java` | Swing, controlador, temas, animaciones y sonido |
| `src/desktop/resources/levels` | Cinco niveles distribuidos |
| `src/desktop/resources/assets` | Sprites PNG y efectos WAV |
| `src/test/java`, `src/desktop-test/java` | Pruebas del modelo y la presentación |
| `scripts` | Ejecución, pruebas y verificadores documentales |
| `docs` | Requisitos, arquitectura, decisiones, trazabilidad y pruebas |

## Entregables

- [Video de gameplay](entregables/VIDEO_GAMEPLAY.mp4).
- Informe técnico: [PDF](entregables/INFORME_TECNICO.pdf) y [DOCX editable](entregables/INFORME_TECNICO.docx).
- Portada del equipo: [PDF](entregables/PORTADA_EQUIPO.pdf) y [DOCX](entregables/PORTADA_EQUIPO.docx).
- [Índice de nueve diagramas PNG y fuentes PlantUML](docs/architecture/uml/pr17-rc-ffa27f9/README.md).
- [Evidencias de pruebas Windows](entregables/VALIDACION_WINDOWS.md), [manifiesto SHA-256](entregables/SHA256SUMS.txt) e [índice de entrega](entregables/README.md).

## Pruebas y uso de IA

La ejecución Windows del 13/11/2026 registró **147 comprobaciones Core, 177 Desktop y 24 UI**, con JDK 26.0.1 y compilación `--release 17`. Son resultados históricos; su alcance, comandos y salidas están en la [estrategia de pruebas](docs/testing/TEST_STRATEGY.md). Una revisión documental no representa una nueva ejecución de esas suites.

Se utilizaron ChatGPT y Codex como apoyo para analizar alternativas, revisar código, preparar pruebas y documentación y producir parte de los recursos visuales. El informe técnico declara ese uso. El [registro de recursos](docs/assets/ASSET_POLICY.md) describe la procedencia de los 31 PNG y 12 WAV.

## Defensa y demostración

[Presentación y guion para cinco integrantes](entregables/README.md). En Ubuntu: `bash INICIAR_JUEGO.sh` (menú) o `bash DEMO_CARGAR_TXT.sh` (nivel externo). En Windows: `INICIAR_JUEGO.cmd` y `DEMO_CARGAR_TXT.cmd`. Consultar [operación y requisitos](docs/demo/DEFENSA_OPERACION.md).
