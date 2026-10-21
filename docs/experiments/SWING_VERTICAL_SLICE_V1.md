# Primer incremento Swing jugable — V1

AS_OF = 2026-10-21. Dominio de recuperación: SOKOBAN_SWING_V1_ISOLATED_RECOVERY.
Incremento original: 2026-10-15, SOKOBAN_SWING_UI_INTEGRATION.
Base histórica: be490aecb2bbcb92b863778a63499c587313146c.
Estado: IMPLEMENTED_EXPERIMENTAL; aceptación grupal no registrada.

## Alcance y ejecución

Ventana 2D con paredes, suelo, destinos, jugador y cajas normales. Las cuatro flechas delegan al motor existente. MOVED suma un movimiento; PUSHED suma un movimiento y un empuje; BLOCKED conserva estado, contadores y presentación. Son contadores demostrativos de esta sesión: no definen puntaje, Undo ni sus métricas. Abrir correctamente otro TXT inicia una demostración nueva; una carga fallida conserva la anterior.

Desde la raíz, con un JDK completo existente que admita release 17 y Windows PowerShell 5.1 o PowerShell 7:

```powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK-verificado>'
powershell -NoProfile -File .\scripts\run-swing.ps1
powershell -NoProfile -File .\scripts\run-swing.ps1 -LevelPath 'C:\niveles\mi nivel.txt'
powershell -NoProfile -File .\scripts\run-swing.ps1 -Test
```

También admite pwsh en Windows; el runner busca `java.exe` y `javac.exe`, por lo que no es un comando Linux nativo. El runner compila con javac --release 17 --limit-modules java.base,java.desktop -encoding UTF-8 -Xlint:all -Werror, en un temporal propio que elimina al cerrar la aplicación o terminar los tests. Selección SPIKE_JDK_HOME → JAVA_HOME → PATH; no instala ni cambia configuración persistente. Desde otro CWD usar la ruta del script; LevelPath relativo se interpreta respecto del CWD del invocador. El modo Test utiliza siempre el TXT demostrativo versionado. No hay build de producto ratificado ni runner Swing Unix en esta iteración.

El TXT predeterminado está en [demo.txt](../../src/desktop/resources/levels/demo.txt); no está embebido en Java. El botón Abrir TXT permite seleccionar un archivo externo. Se reutiliza sin ampliar la gramática experimental: # pared, espacio piso, . destino, @ jugador, $ caja, + jugador sobre destino, * caja sobre destino. UTF-8, LF/CRLF y límites siguen a LevelLoader/LevelParser. Símbolos de entidades especiales o formatos incompatibles producen un error de carga explícito; nunca se simulan sus reglas.

## Fronteras y alternativas

- Model: LevelLoader/LevelParser/LevelBuilder y Board/Cell existentes; MovementEngine/Direction/MoveResult existentes. Ningún archivo del core, sus tests, fixtures ni runners fue modificado.
- Controller: GameController conserva una GamePresentation con la referencia al Board vigente y los dos contadores. Coordina carga y transiciones sin calcular colisiones, analizar TXT ni dibujar.
- View: SwingApplication crea la ventana en el Event Dispatch Thread; ArrowBindings traduce cuatro key bindings WHEN_IN_FOCUSED_WINDOW; BoardPanel pinta los valores del Board. GameView es la frontera mínima que permite probar la coordinación sin una pantalla ni JFrame.
- GamePresentation expone directamente el Board inmutable del core. La vista retiene esa referencia para pintar; no tiene una segunda grilla mutable ni un motor propio. BLOCKED ni siquiera emite otro render.

Fuentes gráficas en src/desktop/java y tests en src/desktop-test/java: esta separación preserva las pruebas del core que compilan y ejecutan con sólo java.base. El PR #4, integrado antes de esta recuperación, además restringe el runner core a los paquetes level/movement aunque aparezca UI en otra ubicación bajo src/main/java. Ambos mecanismos son complementarios; el runner Swing compila separadamente con java.desktop. No se añaden frameworks, GameSession redundante, game loop, polling ni infraestructura de patrones.

La construcción, selección y actualización de componentes se hace en el EDT. La lectura de TXT local, acotada a 65.536 bytes por el loader, es síncrona en el EDT para este spike; un filesystem lento o remoto puede demorar la ventana. Si se necesita carga remota o cancelación, evaluar SwingWorker en una iteración específica. No hay acceso concurrente al controlador.

[UML del subconjunto](../architecture/uml/swing-playable-spike-v1.puml). El modelo integral candidato y sus propuestas PREFERRED permanecen separados.

## Validación histórica ejecutada en Windows

2026-10-15 UTC, Windows 25H2 x64, Temurin 21.0.12.1, release 17:

- Core: Windows PowerShell 5.1, test-level-loading.ps1 -IncludeMovement; Loading 40/40, Movement 31/31, salida 0. Una ejecución del core en esta iteración.
- Integración: run-swing.ps1 -Test; 16/16, salida 0 en Windows PowerShell 5.1 y PowerShell 7.6.5. Casos: demo desde archivo, TXT externo modificado/recargado, cuatro MOVED, PUSHED sobre piso/destino, BLOCKED pared/dos cajas, bindings Swing reales en EDT, correspondencia Board/vista e inmutabilidad, contadores y recarga, archivo ausente, símbolo no soportado y pintura sin pantalla.
- GUI nativa observada e interactuada: demo inicial jugador (2,2), caja (2,4), destino (2,6); Right → (2,3), contadores 1/0; Right → empuje, 2/1; Right → caja sobre destino, 3/2; Up → (1,5), 4/2; Up → pared, sin cambios, 4/2. Coordenadas base cero. Capturas y exit code 0 al cerrar conservados en la evidencia externa de entrega.
- Pintar en BufferedImage es un control headless; no se presenta como evidencia visual. La secuencia anterior sí se ejecutó sobre la ventana Swing real.

También pasó una exportación limpia del índice con rutas de espacios/ñ, CWD externo, TEMP/TMP propios y TXT externo Unicode; 16/16 en PS5.1 y GUI nativa con la misma secuencia. El runner eliminó su temporal; sólo quedó hsperfdata vacío, atribuible a la JVM. Los logs, capturas y el SHA final se entregan fuera del repositorio. No se incorporan rutas personales ni evidencia binaria a Git. En el incremento original del 15/10, Linux/macOS Swing estaban NOT_RUN; la nueva validación Linux R2 se registra por separado. La compatibilidad de fuentes release 17 no demuestra interacción gráfica ni macOS. La revisión grupal sigue pendiente.

## Límites y continuidad

Graphics2D genera placeholders propios: no satisfacen el requisito académico definitivo de imágenes, no se descargaron assets. No hay hielo, cajas frágiles/llave, cerrojos, Undo, Restart funcional, puntaje, victoria/progresión, audio, persistencia ni extras. Una caja en destino sólo cambia su representación; no dispara victoria.

OQ-01 a OQ-11 siguen OPEN y los patrones conservan sus estados. La integración no ratifica TXT, Java/build, reglas abiertas ni arquitectura. Preparado con asistencia de IA (Codex), requiere revisión y comprensión individual.

Siguiente paso: revisión independiente del commit recuperado y preparación de un futuro Draft PR Swing contra main, que ya incluye core (#3) y aislamiento (#4). Preservar el alcance experimental y el delta exclusivo Swing. Publicación, PR y merge requieren autorización posterior.

## Recuperación Linux R2 — 2026-10-21

Original: `1303a33748e129f3d8a55ab2b1799518aaa8df5d`, padre `be490aecb2bbcb92b863778a63499c587313146c`; base integrada: `ee83f9ebd30ee1b553c555c89d9ea453131d6678`. La API confirmó ambos PR fusionados y repositorio público. Se aplica sólo el delta Swing y se reconcilian documentos vivos; Java, pruebas, TXT demo y runner Windows Swing mantienen sus bytes originales. Los runners core del #4, sus fuentes, tests y fixtures se preservan. El SHA final se registra en el informe externo y el historial, sin autorreferenciar este documento.

Entorno de validación: Linux x86_64, OpenJDK/java/javac 17.0.20.1, release 17, locale de proceso `C.utf8` verificada; módulos java.base y java.desktop disponibles. No se instala ni se cambia configuración persistente.

Core, desde la raíz del checkout y con un JDK completo existente:

```bash
LC_ALL=C.utf8 SPIKE_JDK_HOME='<directorio-del-JDK-verificado>' bash scripts/test-level-loading.sh --movement
```

Desktop se compila directamente en un temporal exclusivo externo. Comando equivalente reproducible, desde la raíz, sin crear un runner versionado:

```bash
(
    set -euo pipefail
    export LC_ALL=C.utf8
    jdk='<directorio-del-JDK-verificado>'
    desktop_temp="$(mktemp -d /tmp/sokoban-desktop-r2.XXXXXXXX)"
    cleanup() {
        find "$desktop_temp" -type f -delete
        find "$desktop_temp" -depth -type d -empty -delete
    }
    trap cleanup EXIT
    mkdir "$desktop_temp/classes"
    find src/main/java src/desktop/java src/desktop-test/java -name '*.java' -type f |
        LC_ALL=C sort > "$desktop_temp/sources"
    "$jdk/bin/javac" --release 17 --limit-modules java.base,java.desktop \
        -encoding UTF-8 -Xlint:all -Werror -d "$desktop_temp/classes" @"$desktop_temp/sources"
    "$jdk/bin/java" --limit-modules java.base,java.desktop -Djava.awt.headless=true \
        -Djava.io.tmpdir="$desktop_temp" -cp "$desktop_temp/classes" \
        ar.edu.uade.sokoban.desktop.DesktopIntegrationTest src/desktop/resources/levels/demo.txt
)
```

Validación R2 ejecutada sobre el árbol combinado antes de su commit: **Loading 40 PASS / 0 FAIL; Movement 31 PASS / 0 FAIL; Desktop 16 PASS / 0 FAIL**. Compilación estricta y ambas ejecuciones con salida 0; sin warnings, sin cambios Java y temporales propios eliminados. Los logs y comandos exactos quedan en el informe externo de recuperación. GUI interactiva Linux y macOS nativo: NOT_RUN. La evidencia Windows anterior corresponde al original; no se reprodujo durante esta recuperación ni certifica el parche del PR #4 en Windows.
