# Contribuciones

Trabajar en una rama propia y proponer cambios mediante pull request a `main`. Mantener el modelo independiente de Swing, una sola autoridad de estado y responsabilidades concretas; justificar patrones por el problema que resuelven.

Antes de publicar documentación, revisar rutas y enlaces y ejecutar:

```bash
git diff --check
git diff --cached --check
bash scripts/verify-repo-readonly.sh
python -B scripts/verify-uml-pr17-readonly.py --root . --verify-java
```

Los cambios funcionales requieren las pruebas pertinentes descritas en [la estrategia](docs/testing/TEST_STRATEGY.md). No reatribuir resultados históricos a ejecuciones nuevas. Registrar la procedencia y los derechos de cualquier recurso incorporado y declarar asistencia de IA en la documentación académica.

El README contiene los requisitos para jugar; las herramientas de QA son opcionales para quien solo ejecuta el juego. El [índice documental](docs/README.md) organiza la arquitectura y los entregables.
