# Investigación GitHub histórica

`RESEARCH_SNAPSHOT = 2026-09-26`, `STATUS = HISTORICAL_REFERENCE`, `CURRENT_CODE_OR_LICENSE_AUDIT = NOT_PERFORMED`.

Las conclusiones siguientes se preservan del ZIP/cápsula; no son afirmaciones verificadas sobre HEAD o licencia actuales de terceros. No se copia código ni se selecciona una dependencia. Se deberá revalidar repositorio, versión, fuente y licencia antes de reutilizar.

| Referencia | Análisis reportado y conclusión útil | Límite |
|---|---|---|
| [qnicondavid/sokobani](https://github.com/qnicondavid/sokobani) | Core separado, estado, motor, MoveResult/GameSession, eventos, parser y tests; Undo mediante operaciones inversas muestra alternativa a snapshots | MIT fue reportada históricamente, no comprobada hoy |
| [mciccale/sokoban](https://github.com/mciccale/sokoban) | Java/Swing/MVC y niveles externos cercanos al stack; análisis previo alertó sobre Controller amplio, checks por tipo y estado compartido | No trasladar esas observaciones a una auditoría del código actual |
| [nuno-faria/sokoban-game](https://github.com/nuno-faria/sokoban-game) | Snapshots y mecánicas/muros especiales sugieren que Memento puede simplificar reversión de estado complejo | No prueba reglas oficiales ni justifica copia |
| [ZhengtianChu/Sokoban](https://github.com/ZhengtianChu/Sokoban) | Catálogo comparativo MVC/Factory/Adapter/Singleton/tests; estudiar problema y coste de cada patrón | No aplicar todos por aparecer en otro juego |
| [janbodnar/Java-Sokoban-Game](https://github.com/janbodnar/Java-Sokoban-Game) | Swing, teclado, sprites y render como referencia de presentación | No arquitectura completa; BSD-2-Clause reportada históricamente, pendiente revalidar |
| [Thelin90/Advanced-Object-Oriented-Project-Sokoban](https://github.com/Thelin90/Advanced-Object-Oriented-Project-Sokoban) | Comparación MVC/framework 2D/generalización; una abstracción posible no implica necesidad del TPO | Referencia del documento research del ZIP |

Síntesis del análisis: reglas fuera de UI, transiciones explícitas, estados reversibles y niveles externos; comparar operación inversa con Memento y simplicidad con generalización. Las decisiones locales deben fundamentarse en la consigna y el problema del equipo.

Sin licencia verificable, referencia conceptual solamente. Con licencia, registrar URL/commit/fecha y términos, atribuir si corresponde, adaptar, comprender y probar. Una licencia histórica reportada no autoriza reutilización actual.
