LOS FORCE PUSH — Entrega académica
Repositorio: https://github.com/smatrauade/Los-Force-Push
Rama: main
Commit ejecutado: ee2681148f8dde7425968b61b19b631e5532bb04
Fecha de preparación: 13/11/2026

INFORME_TECNICO.pdf y .docx: explicación del producto, arquitectura, patrones, decisiones, QA e IA.
VIDEO_GAMEPLAY.mp4: captura auténtica OBS, 7:56.8, H264/AAC, 1280x720, 30 FPS.
diagramas_png y fuentes_plantuml: nueve vistas cada una, conservadas desde Git. No son StarUML.
GUION_DEFENSA.txt: exposición de 18 minutos. BORRADOR_CORREO.txt: completar nómina y CC reales; no enviado.
PROCEDENCIA.json: versión, evidencia y límites. SHA256SUMS.txt: integridad de todos los archivos anteriores.

Ejecución Windows: clonar en carpeta nueva, comprobar java y javac del JDK completo y ejecutar powershell.exe -NoProfile -File .\scripts\run-swing.ps1 desde el clon. Para esta QA se usó JDK 26.0.1 con release 17; no se acredita runtime 17 nativo.

Límites de cierre: audio humano pendiente; el MP4 concatena el recorrido principal y un complemento auténtico del selector, sin gameplay generado. Completar integrantes/direcciones reales antes del correo. La evidencia de derechos de assets y la equivalencia formal PlantUML/StarUML no se certifican por hashes.

UML: el verificador sobre checkout Windows detectó finales CRLF en fuentes puml; se empaquetaron los blobs Git exactos y se verificaron sus 18 hashes y 50 referencias Java. No se modificó main para conseguir el resultado.
