# Recursos, procedencia, licencia e IA

## Procedencia real

El paquete contiene **31 sprites PNG RGBA de 32x32**: 29 nombres compatibles
con la version original y 2 variantes opcionales de cajas. Los ladrillos,
pisos y sprites auxiliares conservan los mismos pixeles originales. Las
cajas, los destinos y el operario de casco amarillo son la version final
redisenada mediante un proceso iterativo de pixel art asistido por IA.
No se incluyen sprites descargados de terceros.

Los **12 WAV** son los efectos originales del Punto 5 (PCM 16 bits / 22050 Hz).
`docs/punto5/generar_assets.py` ahora **solo regenera efectos de sonido**;
no pisa los PNG finales. El runtime Java no requiere Python ni Pillow.

Las imagenes de `PREVIAS` pertenecen a los ZIP historicos externos, no al clon; son muestras visuales, no archivos necesarios
para ejecutar el juego. `PANTALLA_JUEGO.png` y `PANTALLA_MENU.png` son vistas
actualizadas sobre las capturas de la interfaz original; no son evidencia
de una nueva ejecucion fisica de Swing.

## Estado de derechos / requisito UADE

La consigna exige **recursos de dominio publico**. El origen original y la ausencia
de material de terceros reducen problemas de atribucion, pero **no constituyen
por si solos una comprobacion juridica de dominio publico o CC0** en toda
jurisdiccion. El equipo debe revisar la titularidad y documentar la licencia
antes de la entrega. Si son titulares de estos recursos, pueden formalizar una
renuncia/dedicacion compatible con CC0 1.0: https://creativecommons.org/publicdomain/zero/1.0/
El documento `ASSETS_REGISTRO_SHA256.csv` identifica cada archivo y su hash.
La decision y declaracion final deben ser hechas por el equipo, no se inventa
firma ni autorizacion de miembros.

## Uso de IA para el informe tecnico

> Se uso ChatGPT (OpenAI) como apoyo en el diseno de la interfaz Swing,
> generacion programatica de sprites y efectos de sonido originales,
> documentacion, scripts de instalacion y revision automatizada. El equipo
> reviso y adapto el resultado, realizo sus propias pruebas y es responsable
> de comprender y defender cada decision de diseno y linea de codigo utilizada.

El ultimo renglon (revision/pruebas del equipo) debe afirmarse **solo despues** de
que efectivamente lo realicen. Registrar evidencia en el PR. La consigna exige
explicitar IA en el informe tecnico.
