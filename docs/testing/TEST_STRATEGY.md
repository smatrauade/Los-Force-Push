# Estrategia de pruebas futuras

Revisado 2026-10-14 (carga experimental R1–R4). La base integrada del bootstrap no contiene tests de dominio ejecutables; esta rama incorpora únicamente los tests experimentales descritos al final. Los casos siguientes son **planificados**, no resultados. Herramienta/framework y Java dependen de OQ-11. Core se prueba sin Swing.

| Área | Casos previstos | Gate |
|---|---|---|
| Carga | TXT válido creado por otra persona; modificar carácter y recargar; archivo faltante; símbolo desconocido; filas/dimensiones; cero/varios jugadores; metadata/resistencia y IDs inválidos | OQ-06/07/08; gramática ratificada |
| Movimiento | Libre; pared; límites; muros cerrados/abiertos; una caja; dos cajas; no tirar; intento inválido sin mutación | RF-03/04 |
| Hielo | Franja corta/larga; primer espacio normal libre; jugador no desliza; pared/caja/muro al salir | OQ-02 para salida bloqueada |
| Fragile | Decremento por empuje; cero; sin resistencia negativa; interacción con hielo; Undo/Restart | OQ-03/06 |
| Key/Lock | Llave activa, normal no activa; muros asociados; retirar llave; varios cerrojos; Undo restaura muros y suscripciones | OQ-01/07 |
| Undo | N-5/N-10/N-15; historial >15; cuarto uso; <5; nuevo movimiento/intento inválido; fragile/muros; snapshots aislados; Restart/métricas | OQ-04/05 y D-008 |
| Victoria/progreso | Todas las cajas; llave/cerrojo; próximo nivel y final del catálogo | OQ-01 y decisión de catálogo |
| Score | Ejemplos tabulados; determinismo; límites; Undo si penalización ratificada | OQ-09 |
| Extras | Pruebas positivas/bordes/invalidez y evidencia del patrón | OQ-10 |
| Presentación | Flechas, imágenes de todo elemento, HUD, botones, resumen, audio | Build ejecutable y assets auditados |

Una mecánica se cierra con regla documentada, ejemplo positivo, caso límite, invalidación/transición atómica, implementación identificable y evidencia ejecutada. Un nombre de clase o UML no cumple aceptación.

Para comparar Builder y construcción directa, mantener mismo contrato TXT e invariantes, probar entradas válidas/inválidas y evaluar cohesión, claridad, coste de abstracción y defensa. No elegir el patrón porque el spike compila.

La evidencia [histórica](HISTORICAL_EVIDENCE.md) no sustituye estas pruebas. Una ejecución nueva registra fecha, commit, entorno/comando, casos, resultado y limitaciones. Manual Swing cubre representación/interacción, no reemplaza pruebas de reglas.

Para documentación actual: `bash scripts/verify-repo-readonly.sh`, enlaces/rutas, archivos requeridos, checks Git, identidad de históricos, revisión manual de autoridad/decisiones/evidencia. No instalar CI ni asumir toolchain Java.

## Pruebas ejecutables del experimento local R1

La tabla anterior conserva los gates de producto final. La tarea actual autorizó un contrato **experimental** sin cerrar OQ-08/OQ-11. [LEVEL_LOADING_R1](../experiments/LEVEL_LOADING_R1.md) registra las ejecuciones R3 de 38 casos y R4 de 40 casos PASS / 0 FAIL en JDK 17 y JDK 26 con archivos reales, UTF-8/EOL, dimensiones, jugadores, errores, determinismo, no publicación parcial, aislamiento e independencia de Swing. Comando: `SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh`.

Runner sin dependencias ni assertions desactivables: falla con código no cero si compila con warnings o si falla un caso. Fixtures mutables y clases se crean en un temporal propio fuera del checkout. En esta rama sí hay tests de carga ejecutables; no hay tests productivos de gameplay. La base histórica precede al incremento: reproducir con las fuentes de esta rama. El runner también admite JAVA_HOME si SPIKE_JDK_HOME no está definido. No se ejecutan A/B históricos ni se alteran sus registros.

R3 conserva los 30 casos originales, refuerza el 25 y agrega 31–38. La causa de tamaño se distingue de estructura; al desactivar exclusivamente el guard de bytes en una copia temporal, 25/38 fallan con salida no cero. Ancho/alto/rectangularidad tempranos, nulos y columna negativa están cubiertos. Es evidencia experimental, sin ratificación de OQ-08/OQ-11 ni reproducción de A/B históricos.

R4 preserva los 38 casos y agrega 39 (archivo externo con nombre Unicode/espacios, LF/CRLF, piso significativo y recarga) y 40 (diagnóstico estable bajo localización de formato distinta, restaurada al terminar). Linux ejecutó la suite con JDK 17/26, desde otra ubicación y una copia del checkout/TMPDIR con espacios/Unicode; JDK 17 también con `JAVA_TOOL_OPTIONS=-Dfile.encoding=US-ASCII`. El mecanismo de argfile UTF-8 sin BOM y rutas absolutas entre comillas del runner Windows se comprobó con javac en Linux; esto no ejecuta PowerShell ni Windows. Controles negativos del runner Unix: JDK seleccionado inexistente, TMPDIR dentro del checkout y fixture erróneo producen salida no cero; sus temporales propios se limpian.

Windows usa `powershell -NoProfile -File scripts/test-level-loading.ps1` o `pwsh` con el mismo archivo; no duplica tests Java ni necesita Python/Bash. Windows/macOS nativos siguen NOT_RUN, con tareas explícitas en TODO. La revisión estática PowerShell 5.1/7 y Bash 3.2/GNU/BSD no sustituye pruebas nativas. [Instrucciones y matriz](../experiments/LEVEL_LOADING_R1.md).
