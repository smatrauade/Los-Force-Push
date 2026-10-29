# Bibliografía y materiales

Revisado 2026-10-29. Las referencias bibliográficas y sus aplicaciones inicialmente se registraron el 08/10 a partir del prompt/cápsula; mantener su carácter reportado, sin atribuir lectura directa de libros o clases no verificada. No se redistribuyen obras ni materiales completos.

| Autor | Obra | Aplicación concreta reportada |
|---|---|---|
| Craig Larman | UML y Patrones, 2.ª edición | GRASP: Expert para Board, Controller para coordinar, Creator, bajo acoplamiento, alta cohesión y asignación de responsabilidades |
| Rebecca Wirfs-Brock y Alan McKean | Object Design: Roles, Responsibilities, and Collaborations | RDD, roles/colaboraciones y límites de Session, motor, parser y View |
| Erich Gamma, Richard Helm, Ralph Johnson y John Vlissides | Design Patterns: Elements of Reusable Object-Oriented Software | Intención/participantes/consecuencias GoF; Builder/Memento/Observer/Strategy frente a alternativas simples |
| Grady Booch, James Rumbaugh y Ivar Jacobson | The UML Reference Manual | Semántica de asociaciones, composición, interfaces y multiplicidades; firmas comunicables |
| Ivar Jacobson, Grady Booch y James Rumbaugh | The Unified Software Development Process | Iteración acotada, riesgos, arquitectura y trazabilidad; no implementar ambigüedades primero |
| Nick Samoylov | [Learn Java 12 Programming](https://www.packtpub.com/en-us/product/learn-java-12-programming-9781789958485) | Referencia de implementación Java; el título Java 12 no decide la versión target |

## Cátedra y aclaraciones

La consigna `PDS - TI - Prof Pepe.pdf` es autoridad principal; su descripción normalizada está en REQUIREMENTS. El PDF original no se distribuye con el repositorio: se recibió y verificó su SHA-256 en una revisión externa del 21/10, registrada en SOURCE_REGISTRY. La normalización autónoma para un nuevo agente está en REQUIREMENTS; no atribuir al clon acceso a los bytes originales.

Históricamente se registraron materiales de **Clases 1–6 y 8–10**, introducción UML, práctica DIP y **Resort de lujo**. En el material externo recibido el 29/10 figuran también Clases 11–13 dentro del corpus aportado y siete transcripciones ASR fechadas; esos originales no están en el clon ni su mera disponibilidad demuestra lectura y aplicación al producto. La procedencia completa se conserva en SOURCE_REGISTRY; no se atribuye contenido a Clase 7 ausente.

Enseñanzas normalizadas:

- SOLID: SRP, OCP, LSP, ISP y DIP; contratos correctos, variación real y no interfaces ceremoniales.
- GRASP: Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection y Protected Variations; responsabilidad donde están los datos.
- MVC: reglas en Model, presentación/entrada en View, coordinación en Controller.
- GoF creacionales: Singleton, Factory Method, Abstract Factory, Builder y Prototype; estructurales: Adapter, Bridge, Composite, Decorator y Facade; comportamiento: Command, Mediator, Memento, Observer, State, Strategy y Template Method.
- Clase 9 profundizó Builder/Prototype; la construcción paso a paso desde TXT respalda Builder como candidato, sin copiar el patrón literalmente.
- Clase 10 introdujo Adapter/Decorator; no obligan a aplicarlos sin problema real.
- Introducción UML: modelar responsabilidades, relaciones y firmas para discutir antes de implementar.
- Práctica DIP: abstraer lo que varía y depender de frontera útil, no volver toda clase interfaz.
- Resort de lujo: análisis histórico relaciona reservas parte-todo/Composite, precio/Strategy, paquete/Builder, habitación/State, legado/Adapter, checkout/Facade y avisos/Observer. Son analogías del análisis, no requisitos Sokoban.

Las transcripciones completas y ejemplos comprimidos de cátedra no están aquí. Su resumen reporta evitar Controllers monolíticos, datos duplicados y checks por tipo, revisar PRs por otra persona, justificar patrones por problema/solución/consecuencias y revisar IA. No se atribuye cada enseñanza a una clase específica sin fuente disponible.

Las [referencias GitHub](GITHUB_REFERENCES.md) tienen menor autoridad que decisiones explícitas del equipo. Toda nueva fuente utilizada se registra con disponibilidad y efecto sobre decisiones; no basta acumular títulos.

La autoría de Learn Java 12 Programming se comprobó en la ficha de Packt el 2026-10-08; no se leyó ni redistribuyó el libro.
