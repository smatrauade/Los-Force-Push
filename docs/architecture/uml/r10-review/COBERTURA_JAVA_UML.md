# Cobertura bidireccional Java ↔ UML — R10

Commit fuente `e7d4c33f3fb17bd3a72bca25ddee1ad37f529087`.

El inventario de GitHub consultado en este chat (`git trees` completo, `truncated=false`) contiene **50 unidades Java productivas** (`src/main/java`: 30, `src/desktop/java`: 20). Las 50 aparecen en las vistas/modelo UML. Los 82 tipos UML del archivo `.mdj` se componen de tipos top-level, tipos anidados y 4 tipos externos del JDK; no son 82 archivos Java.

**Comprobación automatizada de identidad:** los 50 paths del inventario de GitHub coinciden exactamente con los 50 paths de producción extraídos de `TRAZABILIDAD_TIPOS.csv`. Resultado `50/50`, sin fuentes de producción ausentes ni agregadas.

**Límite de la afirmación:** la inclusión de cada fuente en el modelo NO prueba que todos sus métodos/campos se hayan reproducido ni que todas las asociaciones/transiciones sean exhaustivas. La comprobación de relaciones y patrones se basa en inspección del código, registrada en `INFORME_R10_SANDBOX.md` y `AUDITORIA_ARQUITECTURA_R10_PREVIA.md`. Un diagramador no sustituye pruebas Java ni aceptación académica.

## Inventario top-level verificado

| Paquete | Unidades de producción cubiertas |
|---|---:|
| `ar.edu.uade.sokoban.level` | 15 |
| `ar.edu.uade.sokoban.movement` | 6 |
| `ar.edu.uade.sokoban.session` | 9 |
| `ar.edu.uade.sokoban.desktop` | 20 |
| **TOTAL** | **50/50** |

### Próxima comprobación que requeriría herramientas fuera del sandbox

`STARUML_NATIVE_OPEN_SAVE_REOPEN` sigue `NOT_RUN`; no convertir 50/50 a un PASS de la aplicación StarUML ni de los requisitos globales del TPO.
