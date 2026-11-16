# Recursos visuales y sonoros

El juego distribuye **31 sprites PNG RGBA de 32×32** y **12 efectos WAV PCM mono de 16 bits a 22050 Hz**. `PixelAssets` y `SoundManager` los leen del classpath. Los temas modifican la presentación en memoria; no reemplazan archivos. Python y las herramientas de generación no son dependencias para jugar.

El [registro individual](../punto5/ASSETS_REGISTRO_SHA256.csv) identifica ruta, SHA-256, procedencia declarada, estado de licencia y uso de cada recurso. Se conserva como fuente de identificación de los 43 archivos, sin crear un registro paralelo. La [declaración de uso de IA y procedencia](../punto5/USO_IA_Y_LICENCIAS.md) amplía el proceso de producción.

La procedencia registrada es generación original/programática con asistencia de IA; no se reporta descarga de sprites de terceros. Este dato describe origen y no constituye por sí solo una licencia o dedicación jurídica. La exigencia académica de dominio público está en [requisitos](../requirements/REQUIREMENTS.md); los hashes acreditan integridad, no derechos.

El generador [generar_assets.py](../punto5/generar_assets.py) produce SFX y no sobrescribe los PNG finales. No hace falta ejecutarlo para descargar o jugar. Los créditos corresponden al equipo Los Force Push; la asistencia utilizada se declara en el informe técnico.

## Verificación técnica

El [verificador documental](../../scripts/verify-repo-readonly.sh) comprueba registro completo, rutas, duplicados, faltantes, hashes y formato de PNG/WAV. Limita la descompresión de sprites a la dimensión esperada y conserva el máximo de 10 MiB por archivo. Los renders UML y los documentos de entrega tienen listas controladas distintas de los assets del juego.

Cualquier incorporación de un recurso debe registrar origen, autor/titular cuando corresponda, términos comprobables y hash, además de mantener formatos y pruebas. La [regresión](../../scripts/test-verify-repo-readonly.py) prueba los rechazos en copias temporales, sin alterar los archivos del juego.
