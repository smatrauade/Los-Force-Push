# Los Force Push / Sokoban — Guion de defensa para cinco integrantes

**Asignatura:** Procesos de Desarrollo de Software — UADE
**Docente:** Jonathan Leonel Pepe
**Fecha de exposición:** miércoles 18 de noviembre de 2026
**Objetivo temporal:** 18 minutos de exposición y demostración en vivo (margen de 15–20 minutos)
**Estado del documento:** guion de trabajo para la defensa; reparto y tiempos sujetos a ensayo final.

## 1. Criterios de la defensa

Las indicaciones de la cátedra establecen una **DEMO en vivo**, explicación de funcionalidades y patrones de diseño, con duración de **15 a 20 minutos**. No es obligatorio mostrar código ni diagramas en pantalla. El modelo StarUML, informe y demás entregables se mantienen disponibles para consulta y evaluación académica, aunque no se proyecten.

La estructura expositiva de **siete bloques** se utiliza como **índice conceptual para explicar**. No debe confundirse con los **nueve diagramas de clases** existentes en el `.mdj` final. Las siete secciones conceptuales son: (1) Arquitectura MVC; (2) Modelo y movimiento; (3) Sesión, Undo y puntaje; (4) Carga TXT; (5) Abstract Factory; (6) Command; y (7) Observer.

Las dos funcionalidades **adicionales** que se defienden son **temas visuales / Abstract Factory** y **Replay / Command**. **Observer** se utiliza para comunicar los cambios de cerrojos, que son parte de las mecánicas requeridas. **Undo** se implementa mediante snapshots internos; no debe describirse como un GoF Memento formal. La lógica de apertura/cierre de muros pertenece al modelo, no a los observadores. No afirmar que existe persistencia de partidas: los botones Guardar/Cargar están deshabilitados.

## 2. Distribución propuesta y cronograma

| Tiempo | Integrante | Bloques conceptuales | Evidencia visual/demo |
|---|---|---|---|
| 00:00–03:15 | **Lucas Capatti** | Presentación, MVC y carga TXT | Menú; archivo TXT; nivel `demo` y primera victoria |
| 03:15–06:45 | **Santino Matra** | Modelo y movimiento | `campaign-b` (llave/cerrojo), `ice_demo` (hielo), `fragile_demo` (frágil) |
| 06:45–10:15 | **Martina García Rios** | Sesión, Undo, Restart y puntaje | `fragile-ice-demo`, cinco movimientos, Undo y victoria |
| 10:15–13:45 | **Juan Ignacio Antonini** | Abstract Factory y Observer | Selector de tres temas; referencia al cerrojo demostrado |
| 13:45–17:30 | **Giuliano Calicchia** | Command / Replay y decisiones de diseño | Replay en partida terminada: Step/Reset/Play/Back |
| 17:30–18:00 | **Lucas (cierre)** | Resultado y enlace a entregables | Pantalla final / portada breve |

**Distribución orientativa.** Cada integrante debe comprender todas las decisiones y patrones, no solamente su bloque.

**Operador único recomendado:** una persona maneja teclado y mouse durante toda la demo para evitar traspasos y errores. Los cinco pueden exponer y pedir acciones al operador. Si Lucas opera, que no intente leer, mover y cambiar ventanas simultáneamente durante su sección: ensayar el relevo o preparar una persona de apoyo.

---

## 3. Texto sugerido para exponer, con indicaciones en vivo

### Persona 1 — Lucas Capatti | 00:00–03:15

**Tema: presentación, MVC y carga TXT.**

**Texto oral:**

> Buenas tardes. Somos **Los Force Push** y vamos a presentar nuestro trabajo integrador: una versión de **Sokoban** desarrollada en **Java con Swing**. La idea del juego es mover un personaje por un tablero y empujar cajas hasta sus destinos, respetando obstáculos. A partir de esa mecánica agregamos cajas especiales, hielo, cerrojos, muros dinámicos, seguimiento de movimientos, Undo, puntaje y dos funcionalidades adicionales con patrones de diseño.
>
> Una decisión importante fue separar las responsabilidades. En la **arquitectura MVC**, la vista Swing muestra el tablero, los controles y las animaciones; el **GameController** recibe las acciones del usuario y coordina el caso de uso; mientras que el modelo representa el mundo y determina si cada movimiento es válido. De esta manera, la interfaz no necesita calcular por sí misma cómo se empuja una caja o cuándo se gana.
>
> Los niveles se definen en archivos **TXT externos**. Se leen y validan antes de reemplazar la partida actual. Si un archivo no cumple el formato, el juego informa el problema y mantiene la sesión que ya estaba abierta. El contenido del nivel no está escrito dentro de la interfaz: los parsers construyen una definición válida y a partir de ella se inicia el estado de juego.
>
> Primero vamos a mostrar una partida sencilla. Las flechas permiten desplazarse y empujar; no podemos tirar de las cajas ni mover dos a la vez. Cuando todos los destinos quedan cubiertos, aparece la victoria y su resultado.

**Demostración:** mostrar brevemente **ABRIR NIVEL TXT** y elegir una copia preparada de `demo.txt` desde `src/desktop/resources/levels/demo.txt` mediante el selector (opcional si el tiempo apremia). Volver al menú; elegir **NUEVA PARTIDA / DEMO** y resolver el primer nivel con `→ → →`. Mostrar la pantalla de victoria y el contador. **No intentar abrir el código ni los nueve diagramas.**

**Transición:** «Ahora Santino va a mostrar cómo esas reglas se extienden a los tipos de caja y los terrenos especiales».

### Persona 2 — Santino Matra | 03:15–06:45

**Tema: estado del mundo, motor y movimiento.**

**Texto oral:**

> El tablero no se maneja como una colección de imágenes que se mueven libremente: existe un **RuntimeState** que contiene terreno, posición del jugador, cajas, cerrojos y muros. El **RuntimeMovementEngine** recibe el estado y la dirección de movimiento, comprueba las reglas y genera el nuevo estado. En nuestro diseño trabajamos con estados inmutables para no mezclar la lógica con el dibujo o las animaciones.
>
> Hay tres tipos de caja. La **normal** se empuja hasta una celda disponible. La **llave** puede activar un cerrojo; cuando queda sobre el cerrojo correcto, los muros asociados se abren. Ese cerrojo no equivale a un destino de victoria. La **frágil** tiene una resistencia individual: cada empuje la reduce, y puede romperse al llegar a cero.
>
> En los terrenos de **hielo**, las cajas se deslizan según las reglas del motor. El jugador no se desliza automáticamente. En un empuje, el recorrido completo se decide en el modelo y la animación solamente lo representa. Así evitamos que el dibujo y el cálculo produzcan resultados diferentes.
>
> Esto refleja la idea de **alta cohesión y bajo acoplamiento**: el motor resuelve movimientos, el estado conoce el mundo y Swing presenta el resultado. Una acción bloqueada no cambia el mundo ni aumenta los contadores.

**Demostración:** desde la victoria de `demo`, pulsar **SIGUIENTE NIVEL**. Resolver `campaign-b` con la secuencia validada en pruebas: `→ ← ↓ ↓ → → ↑ → ↑ ← ←`; destacar la llave y el cambio de muro. Avanzar a `ice_demo`, usar `↓ →`, observar deslizamiento y esperar la animación. Avanzar a `fragile_demo`, usar `→ → →`, mostrar el comportamiento frágil. Si se extiende demasiado, dar prioridad a `campaign-b` y `ice_demo` y mencionar el nivel frágil al pasar al siguiente bloque.

**Transición:** «Con ese motor funcionando, Martina va a explicar cómo conservamos el progreso y podemos deshacer movimientos sin recalcular todo».

### Persona 3 — Martina García Rios | 06:45–10:15

**Tema: GameSession, Undo, Restart y cálculo de puntaje.**

**Texto oral:**

> El motor sabe resolver un movimiento, pero no administra toda una partida. Para eso tenemos **GameSession**, que conserva el estado inicial, el estado actual, los contadores y el historial. Cada movimiento exitoso permite guardar una instantánea del mundo y sus métricas reversibles.
>
> El requisito de **Undo** era particular: no retroceder un paso, sino **cinco movimientos exitosos por uso**, con un historial máximo de quince y hasta tres usos consecutivos por intento. Una acción de Undo restaura el estado, los movimientos y los empujes de ese momento; sin embargo, el uso acumulado de Undo sigue contando para el puntaje. Esto no es lo mismo que Replay: Undo restaura snapshots de una partida activa; Replay reproduce comandos en otra partida separada.
>
> **Restart** vuelve al estado inicial del nivel y reinicia el historial y los contadores del intento. La victoria se decide comprobando las cajas y los destinos; luego se presenta un resumen de movimientos, empujes, usos de Undo y puntaje.
>
> El criterio de puntaje que elegimos es **máximo entre cero y mil menos los movimientos, menos dos veces los empujes y menos diez veces los usos de Undo**. Lo concentra una clase **ScoreCalculator**, en lugar de poner esa fórmula dentro de la ventana. Es un ejemplo de asignar una responsabilidad específica a un componente concreto.

**Demostración:** entrar en `fragile-ice-demo`. Esperar la animación del primer empuje. Realizar cinco movimientos exitosos: `→ ↓ → → →`; pulsar **DESHACER** (`Z`), que debe restaurar el inicio. Resolver ahora con `→ ↓ → → → → →`, esperando las animaciones. Mostrar la victoria y la penalización de Undo. Como referencia, sin Undo la solución de siete movimientos se registró históricamente con 989 puntos; con un único uso de Undo y exactamente esa solución, el puntaje esperado es **979**. Si el entorno o el recorrido difiere, explicar la fórmula sin afirmar un número prefijado. Señalar el botón **REINICIAR**, pero no pulsarlo al final: interesa conservar la victoria para la sección Replay.

**Transición:** «Con la lógica y los resultados separados de la ventana, Juan va a explicar cómo cambiamos la apariencia y cómo avisamos los eventos de cerrojos».

### Persona 4 — Juan Ignacio Antonini | 10:15–13:45

**Tema: Abstract Factory y Observer.**

**Texto oral:**

> Una de las **dos funcionalidades adicionales** es el cambio de tema visual. Queríamos que los colores de la interfaz y la apariencia de los sprites cambien de manera coherente, sin modificar las reglas del juego. Para eso aplicamos **Abstract Factory**.
>
> La interfaz **VisualThemeFactory** define los productos que debe entregar cada familia: una **UiPalette** para los colores de Swing y una **SpritePresentation** para transformar la representación visual de los sprites. Tenemos tres familias: **Original Cobre, Nocturno y Jade**. El controlador de temas selecciona una familia y actualiza la presentación. La alternativa era distribuir condiciones por tema en muchos componentes; la fábrica concentra esa variación y facilita mantener familias consistentes.
>
> También usamos **Observer** para los cambios de cerrojos. Cuando una transición ya fue confirmada, **LockChangeSubject** compara el estado anterior con el nuevo y publica un evento. Los suscriptores de la presentación lo utilizan para mostrar avisos y reproducir efectos de sonido. **Los observadores no deciden si el muro se abre**: eso lo calcula el modelo. Esta separación evita acoplar las reglas a ventanas o dispositivos de audio.
>
> Los dos casos muestran el mismo criterio de diseño: responsabilidades delimitadas y comunicación mediante contratos, en lugar de acumular todas las decisiones en una sola clase.

**Demostración:** desde la pantalla de victoria, ir a **VOLVER AL MENÚ → OPCIONES**, mostrar Original Cobre, Nocturno y Jade, y volver a la partida terminada por **CONTINUAR** (ruta corroborada por la prueba UI del repositorio). Para Observer, **referirse al cambio de cerrojo ya mostrado en `campaign-b`**; no reiniciar la campaña sólo para repetirlo. Dejar disponible la pantalla de victoria con el botón **VER REPLAY**.

**Transición:** «Por último, Giuliano va a mostrar la segunda funcionalidad adicional: reproducir una partida completa sin modificar la partida original».

### Persona 5 — Giuliano Calicchia | 13:45–17:30

**Tema: Command, Replay y cierre técnico.**

**Texto oral:**

> La segunda **funcionalidad adicional** es **Replay**, que permite observar cómo se resolvió un nivel después de terminarlo. Para implementarlo utilizamos el patrón de comportamiento **Command**.
>
> Cada **MoveCommand** representa una dirección y tiene una operación de ejecución sobre el motor. **ReplayRecorder** registra los movimientos exitosos y ajusta esa secuencia cuando se utiliza Undo o Restart. Al finalizar una partida válida, genera una **ReplayTrace** que puede verificarse. **ReplayPlayer** recibe esa traza y ejecuta los comandos sobre un estado propio, independiente de la sesión que acabamos de ganar.
>
> La decisión importa porque si reprodujéramos sobre la partida real, podríamos alterar el puntaje, los contadores o incluso el avance de niveles. Con el estado aislado, los controles **Play, Pause, Step, Reset y Back** permiten recorrer la secuencia sin tocar el progreso. Además, la reproducción es opcional: si una traza no puede validarse, la victoria sigue siendo válida.
>
> En conjunto, aplicamos **MVC** como arquitectura, principios **SOLID y GRASP** para asignar responsabilidades y los patrones **Abstract Factory, Command y Observer** para resolver problemas concretos. No agregamos patrones sólo por incluirlos: por ejemplo, el puntaje utiliza una sola fórmula, así que no requería Strategy; y Undo usa snapshots internos sin presentarlo como un Memento formal.
>
> La implementación se encuentra en el repositorio público con instrucciones de ejecución, pruebas, informe, video y modelo de clases StarUML. Nuestro objetivo fue entregar un juego funcional, pero también un diseño que podamos explicar, validar y extender.

**Demostración:** en la victoria del último nivel, seleccionar **VER REPLAY**. Mostrar **STEP**, **RESET**, **PLAY**, **PAUSE** y **BACK**, indicando que se usa un tablero independiente. No navegar hacia otra partida antes de mostrar Replay. Si el ritmo queda corto de tiempo, mostrar `STEP → RESET → PLAY → BACK` y explicar Pause verbalmente.

**Transición:** «Lucas, cerramos con una síntesis de lo que se entregó».

### Cierre — Lucas | 17:30–18:00

> Para cerrar: mostramos las reglas del Sokoban, niveles externos, cajas especiales, Undo y puntaje, junto con las dos funcionalidades adicionales, **temas y Replay**. Las decisiones de diseño se documentaron y contrastaron con pruebas. La implementación, los diagramas StarUML y los demás entregables están disponibles en el repositorio. Gracias; quedamos atentos a sus preguntas.

---

## 4. Hoja de operación: demo reproducible

**No leer en voz alta; entregar al operador.** Las siguientes soluciones corresponden a `ReleaseCampaignTest.java` y `ReleaseUiSmokeTest.java` del repositorio y deben ensayarse con la interfaz real antes del miércoles.

| Nivel / efecto | Acciones | Puntaje de solución simple sin Undo |
|---|---|---:|
| `demo` | `→ → →` | 993 |
| `campaign-b` | `→ ← ↓ ↓ → → ↑ → ↑ ← ←` | 983 |
| `ice_demo` | `↓ →` | 996 |
| `fragile_demo` | `→ → →` | 991 |
| `fragile-ice-demo` | `→ ↓ → → → → →` | 989 |

Para demostrar Undo en `fragile-ice-demo`: **primero** `→ ↓ → → →`, luego `Z`, **después** la secuencia completa de siete movimientos. La puntuación prevista en ese recorrido es 979, siempre que se reproduzcan exactamente las acciones indicadas y se complete una sola vez Undo. **Esperar a que finalicen las animaciones** entre acciones relevantes, especialmente en hielo y rotura de cajas. La pantalla debe tener el foco del teclado.

Si no es imprescindible recorrer los cinco niveles durante la clase, reducir las acciones, **no** la explicación de los tres patrones: al menos demostrar un caso de llave/cerrojo, uno de hielo/fragilidad, Undo, un tema alternativo y Replay de un nivel finalizado.

## 5. Control previo y contingencias

- Usar un clon y una versión del juego ya revisados; en Windows el runner oficial desde la raíz del repositorio es `INICIAR_JUEGO.cmd` (doble clic). Requiere JDK completo 17+ y un escritorio gráfico.
- Tener abierto de antemano el juego, probar teclas, volumen, zoom y selector TXT, cerrar notificaciones y conocer la ruta local exacta de los niveles. **Usar los accesos directos de demostración, que compilan y abren el juego automáticamente; no resolver errores en público.**
- Definir un único operador y una señal acordada para pasar de orador o de nivel. Las animaciones deben terminar antes de continuar.
- Si una carga TXT falla, el juego conserva la partida. Explicar el error en vez de prometer que la entrada es válida; usar un TXT probado.
- Si falla la demo en vivo, intentar reiniciarla una sola vez. El video de gameplay ya publicado sirve como **evidencia de respaldo**, pero no debe presentarse como equivalente a haber realizado la demo en vivo.
- No decir que se puede guardar/cargar partidas en disco: esos controles están deshabilitados. No confundir el Replay con Undo ni Observer con la lógica del muro.
- Ensayar con cronómetro y dejar unos 60–90 segundos de margen de movimiento/transiciones dentro de los 18 minutos. Una persona distinta del operador lleva el tiempo.

## 6. Preguntas de defensa posibles (todos deben poder responder)

**¿Por qué MVC?** Para desacoplar las reglas del juego de Swing; el controlador interpreta acciones y delega en el modelo, mientras la vista presenta resultados.

**¿Por qué estados inmutables?** Ayudan a mantener una fuente autoritativa y simplifican la restauración y validación; su coste es crear nuevas estructuras al mover.

**¿Undo es Memento?** No afirmamos una implementación formal de ese GoF. GameSession conserva snapshots de estado y métricas; Replay usa Command.

**¿Qué diferencia a Abstract Factory de cambiar colores con if/switch?** La fábrica proporciona una familia consistente de paleta y tratamiento de sprites mediante un contrato, evitando condicionales repartidos por las vistas.

**¿Quién abre el muro: Observer o RuntimeState?** El modelo determina la apertura. Observer comunica a la vista y el sonido un cambio ya confirmado.

**¿Por qué Replay no modifica el puntaje?** ReplayPlayer ejecuta sobre una copia/estado independiente y no actualiza GameSession ni LevelProgression.

**¿Por qué no Strategy para el puntaje?** No existe un conjunto de políticas intercambiables en la implementación; ScoreCalculator encapsula una sola fórmula de manera suficiente.

**¿El formato TXT garantiza que todo nivel se pueda resolver?** No. La validación garantiza estructura y consistencia según las reglas; solvabilidad general no se demuestra por leer un archivo válido.


## Presentación visual

- [Presentación editable (PowerPoint)](Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx)
- [Copia PDF](Los_Force_Push_Sokoban_Defensa_18-11-2026.pdf)
- [Guía operativa de demo](../docs/demo/DEFENSA_OPERACION.md)

El material visual acompaña la exposición y no sustituye la demostración.
