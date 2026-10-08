# Evidencia experimental histórica

```text
EVIDENCE_CLASS = HISTORICAL_EXTERNAL_EVIDENCE
REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED
CURRENT_DOMAIN_TESTS_EXECUTED = NONE
```

Los resultados se reciben del prompt normalizado y de la cápsula del ZIP. No están los bytes Java de los spikes; no se ejecutaron nuevamente ni se copiaron implementaciones. El Java 21 reportado no fija el target del proyecto.

## Spike A — estado, parser y Memento

Resultado reportado **PASS 8/8** y compilación `javac -Xlint:all` sin warnings según registros previos. Casos descritos en la cápsula:

1. Parsear fixture externo válido.
2. Rechazar múltiples jugadores.
3. Rechazar frágil sin metadata de resistencia en el formato experimental.
4. Limitar historial a 15.
5. Restaurar exactamente cinco movimientos.
6. Restaurar resistencia, muros y contadores.
7. Separar métrica de Undo del Memento.
8. Aislar snapshot de mutación del mapa fuente.

Apoya viabilidad exploratoria, no formato TXT aceptado, política score, semántica completa de Undo ni GUI. No existe evidencia productiva reproducible de estos casos en este repositorio.

## Spike B — movimiento e interacciones

Resultado reportado **PASS 12/12** y compilación sin warnings según registros previos. Casos históricos:

1. Movimiento libre.
2. Pared bloquea sin mutación.
3. Empuje normal.
4. No empujar dos cajas.
5. Hielo hasta primer espacio normal vacío.
6. Salida del hielo bloqueada falla cerrada bajo hipótesis experimental.
7. Fragile pierde una resistencia por empuje mientras desliza.
8. Fragile se rompe en cero.
9. KeyBox activa Lock y abre dos muros mediante Observer.
10. NormalBox no activa Lock.
11. Muro cerrado bloquea.
12. Muro abierto deja pasar.

La hipótesis fail-close del caso 6 no resuelve OQ-02, y abrir dos muros no fija cardinalidad oficial. Las interacciones frágil/hielo pendientes tampoco quedan aceptadas por el spike. La descripción recibida no permite auditar los asserts ni extender sus conclusiones.

## Recuperación futura

Obtener archivos reales, verificar integridad y procedencia, revisar código/licencias/hipótesis, fijar toolchain y ejecutar con comandos registrados. Sólo entonces crear evidencia nueva con commit, entorno, casos y resultado. No reemplazar retroactivamente este relato por una supuesta reproducción.

Las verificaciones actuales son integridad de fuentes y consistencia documental/Git; están separadas de pruebas del juego. No hay tests Java productivos ni build.
