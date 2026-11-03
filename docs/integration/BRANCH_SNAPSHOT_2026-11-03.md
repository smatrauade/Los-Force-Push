# Corte histórico — comparación de ramas (2026-11-03)

`TYPE=IMMUTABLE_REMOTE_SNAPSHOT` · `VERIFIED_GITHUB_READ_ONLY=2026-11-03` · `SOURCE=GITHUB_REST_BRANCHES_AND_COMPARE` · `PR_OPEN=0_AT_CUT`

| Rama | SHA completo al corte | Frente a `main` | Alcance comprobado |
|---|---|---|---|
| `main` | `f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2` | canónica | Subsistema básico y TXT integrado previo a los tres módulos |
| `victoria` | `a7df64172dbb857e7a10709ec9ea85a5f80aacf7` | +1 / −0 | Runtime de KeyBox/Lock/DynamicWall, victoria y progresión |
| `Objetivo2` | `ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb` | +2 / −5 | ICE/Fragile y cambios TXT/GUI; rama divergente |
| `feature/undo-restart-score` | `efd0fcd2a1423bd1d5b064458671ec9b149fd054` | +8 / −0 | Historial/Undo/Restart/Score corregido |
| `impaciencia` | `b2c63327384a9e72333284d935aed181f0c66565` | +12 / −2 | Experimento individual; R10 UML DRAFT, solo referencia |

En este corte **no** se encontró `integration/tpo-unificado-2026-11-03`, ni PR abierto. La verificación no incluye `git status` de los equipos locales ni pruebas Java conjuntas.

La rama inicial prevista parte de `victoria` porque es descendiente de `main`; en I0 sólo quedará incluida esa contribución y este contrato documental. El resto se integrará en incrementos separados. Los SHA de esta tabla **son evidencia de corte, no constantes para toda la vida del proyecto**. Revalidar contra remoto antes de cada escritura.

Consultar [la matriz](INTEGRATION_ACCEPTANCE.md) para inconsistencias reales y [la política de frescura](../continuity/BRANCH_AWARE_FRESHNESS.md) para actualizar el estado vivo sin editar esta fotografía.
