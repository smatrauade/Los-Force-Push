# Punto 5 — Ejecutar desde GitHub (Windows / GitHub Desktop)

Revisado 28/10/2026. **Punto 5 ya está integrado por PR #9; C2-L7, por PR #8. No se reinstala desde ZIP ni se recrea la antigua rama UI.**

## Obtener un clon

En GitHub Desktop seleccionar Clone repository, URL https://github.com/smatrauade/Los-Force-Push, y una carpeta nueva. Abrir main. Si se usa un clon existente, inspeccionar primero sus cambios y worktrees; no sobrescribirlos ni actualizar automáticamente un checkout con trabajo pendiente.

También se puede clonar por terminal:

~~~bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
git rev-parse HEAD
~~~

Comparar el HEAD y los PR actuales con [CURRENT_STATE](../continuity/CURRENT_STATE.md). Los commits documentados son eventos históricos, no un HEAD permanente. Se requiere un JDK completo 17+, no sólo JRE; Maven/Gradle y Java definitivos siguen sin ratificar.

## Compilar, ejecutar y probar

PowerShell 5.1 o 7, desde la raíz:

~~~powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK-completo>'
powershell -NoProfile -File ./scripts/run-swing.ps1
powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/mi nivel.txt'
powershell -NoProfile -File ./scripts/test-level-loading.ps1 -IncludeMovement
powershell -NoProfile -File ./scripts/run-swing.ps1 -Test
powershell -NoProfile -File ./scripts/run-swing.ps1 -UiTest
~~~

Puede usarse pwsh. El runner compila en un temporal propio con release 17 y UTF-8, agrega src/desktop/resources al classpath y limpia al terminar. C2 tiene suite independiente: [TEST_STRATEGY](../testing/TEST_STRATEGY.md). -Test ejecuta Desktop y VisualAssets; -UiTest necesita escritorio y es automatizado, no QA manual.

El menú permite cargar demo.txt o un TXT externo legacy/C2 compatible. Flechas para caminar/empujar, ESC pausa, M audio, +/- zoom. Undo, Restart, guardar/cargar y siguiente nivel siguen deshabilitados sin callbacks reales. La pantalla final está preparada para LevelResult; el core no infiere victoria ni score.

Los 31 PNG y 12 WAV ya están en Git: no descargar ZIP, copiar .git ni regenerar imágenes. Los 29 sprites base y dos variantes opcionales se describen en el [manifiesto](ASSETS_REGISTRO_SHA256.csv). Sus derechos siguen pendientes según [IA/licencias](USO_IA_Y_LICENCIAS.md).

## Integrar módulos y colaborar

[INTEGRACION_EQUIPO](INTEGRACION_EQUIPO.md) localiza UiCommands, GameView, LevelResult y los puntos de adaptación visual. El módulo de lógica entrega resultados y disponibilidad; Swing sólo representa/invoca. C2-L7 aún rechaza atributos y entidades especiales.

Para mantenimiento nuevo crear una rama desde la base acordada, con el checkout limpio y PR acotado según [CONTRIBUTING](../../CONTRIBUTING.md). No volver a publicar los archivos del Punto 5 como un incremento nuevo.

## Problemas frecuentes

- Sprites ausentes: lanzar con el runner o marcar src/desktop/resources como resource root del IDE, sin moverlo.
- javac ausente: seleccionar un JDK completo mediante SPIKE_JDK_HOME; no basta java de un JRE.
- Audio inaudible: comprobar M/opciones y el dispositivo; el fallo de audio no debe abortar el juego.
- Botones pendientes: no exigir operaciones que todavía no tienen servicio.
- Scripts bloqueados: seguir la política local para scripts confiables; no cambiar ExecutionPolicy global.
- Linux/macOS: consultar los comandos y límites del runner Bash en [README](../../README.md).

## Antecedente ZIP

Los paquetes del 26/27 de octubre y su instalador se usaron antes del PR #9. Son antecedentes externos de preparación; no forman parte del procedimiento actual ni son necesarios para reproducir el clon. No aplicar sus archivos sobre una base que ya incluye el incremento.
