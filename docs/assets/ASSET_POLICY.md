# Política de assets

Revisado 28/10/2026. La consigna exige imágenes para el tablero, SFX apropiados y recursos de dominio público. La comprobación de integridad técnica no acredita derechos.

## Recursos presentes

El PR #9 incorporó [src/desktop/resources/assets](../../src/desktop/resources/assets): **31 PNG RGBA de 32×32 y 12 WAV PCM mono de 16 bits/22050 Hz**. Son 29 sprites base y dos variantes opcionales de cajas. Los recursos para hielo, fragilidad, cerrojos y victoria no implementan sus reglas.

El único registro individual es [ASSETS_REGISTRO_SHA256.csv](../punto5/ASSETS_REGISTRO_SHA256.csv): 43 rutas, hashes, procedencia, uso y estado de licencia. No duplicar esa tabla aquí. PixelAssets y SoundManager cargan desde classpath; Python/Pillow y los ZIP originales no son dependencias del runtime.

## Derechos pendientes

[USO_IA_Y_LICENCIAS](../punto5/USO_IA_Y_LICENCIAS.md) declara origen programático/asistido por IA, sin material de terceros reportado, y conserva PENDIENTE_VERIFICACION_CC0_EQUIPO. No existe una dedicación CC0 ni una verificación de titularidad inferida de esos orígenes. El equipo debe documentar autoría/titularidad, decisión/licencia, fecha y responsable antes de afirmar cumplimiento del requisito. El uso de IA debe declararse y revisarse para la defensa.

Kenney y Pixabay son recomendaciones históricas del profesor, no fuentes de estos archivos ni garantía automática de dominio público.

## Incorporaciones y validación

Cada nuevo recurso necesita ruta/nombre, tipo/uso, hash, autor/origen, evidencia y términos de licencia, fecha y responsable. No copiar activos de terceros sin verificar el recurso individual y permiso suficiente.

El [verificador](../../scripts/verify-repo-readonly.sh) sólo acepta PNG bajo assets/sprites y WAV bajo assets/sfx, con nombres explícitos registrados en el manifiesto. Comprueba firma/formato, SHA-256, duplicados, faltantes y archivos no registrados; conserva el límite de 10 MiB por archivo y limita la descompresión PNG a los scanlines de 32×32 RGBA. No descarga ni escribe archivos y no valida licencias por consultar el CSV.

La [regresión](../../scripts/test-verify-repo-readonly.py) altera únicamente copias temporales. [TEST_STRATEGY](../testing/TEST_STRATEGY.md) documenta comandos y evidencia. Al ampliar formatos o dimensiones, revisar contrato, cuotas, pruebas y manifiesto conjuntamente; no desactivar el control binario.
