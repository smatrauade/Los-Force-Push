# Registro de fuentes y disponibilidad

Revisado 2026-10-21 para anexar fuentes posteriores. La tabla conserva la disponibilidad al bootstrap del 08/10. Prioridad: consigna → aclaraciones posteriores → oral → cátedra → equipo → bibliografía → GitHub → inferencia. No mezclar rango de autoridad con disponibilidad de bytes.

| Fuente | Disponibilidad actual | Uso / limitación |
|---|---|---|
| Prompt de misión autorizado el 2026-10-08 | Leído directamente | Control de ejecución y descripción normalizada de requisitos/historia; no texto literal del PDF |
| ZIP SOKOBAN_REPO_BOOTSTRAP_V1_2026-10-07 | Leído; 20 entradas del MANIFEST verificadas por tamaño/SHA-256 | Documentos de continuidad históricos; scripts inspeccionados, no ejecutados para aplicar |
| Cápsula de equipo 2026-10-07 (ZIP y archivo adicional) | Ambos ejemplares son idénticos por SHA-256; bytes preservados en capsules | Resumen reportado, no autorización; evidencia E3 externa, numeración antigua |
| UML v0.5 | Bytes reales del ZIP disponibles y preservados | Candidato, no implementación ni .mdj |
| PDS - TI - Prof Pepe.pdf | NO DISPONIBLE | Requisitos normalizados; SHA histórico reportado abajo, no recalculado |
| Clases 1–6 y 8–10, introducción UML, práctica DIP y Resort | Originales NO DISPONIBLES | Enseñanzas resumidas de prompt/cápsula, no lectura directa nueva |
| Transcripciones/clases | Originales NO DISPONIBLES | Aclaraciones atribuidas indirectamente; no transcripciones completas |
| Discusión equipo 30/09 | Reportada en prompt/cápsula | Métricas Undo separadas TEAM-PROPOSAL, no ratificación |
| Libros técnicos | Originales NO DISPONIBLES; ficha Packt de Learn Java 12 consultada el 08/10 | Identificación y aplicación reportada, sin redistribución |
| Investigación GitHub 26/09 | Resumen disponible; código/licencia actual NO AUDITADOS | Conclusiones históricas, no permiso de copia |
| Spikes Java 21 A/B | Resultados reportados; bytes NO DISPONIBLES | HISTORICAL_EXTERNAL_EVIDENCE; reproducibilidad NOT_VERIFIED |
| Repo del equipo | Auditada API/CLI y clon real al 08/10 | Base, permisos, árbol y README confirmados; revalidar antes de nueva escritura |
| Assets finales y StarUML | NO DISPONIBLES / PENDIENTES | Ninguno inventado |

## Integridad comprobada en esta iteración

- ZIP SHA-256: `8ec010332584c5899d408bfb1bfa4cad8509b519110b527e940a9ae1d2ac99ef`.
- Cápsula preservada: `4250c89752214242f35203237539024cbad54423054ccd0387263100fee61859`.
- UML preservado: `07e86f32bc2ea5c7091508698b415efbf18e9bce45464bb477cb154e5a268901`.

Hash **histórico reportado**, no comprobado hoy, del PDF oficial: `2222cf4ca087946f74a55ea786ca7773a91f42fb5e40c2900cf55b3b51688a49`. Los otros hashes de originales ausentes en la cápsula también son históricos, no integridad comprobada actualmente.

La fecha de la cápsula y sus declaraciones de no autorización reflejan la tarea anterior. En esta integración prevalece la solicitud actual de bootstrap. Las conclusiones nuevas se etiquetan como análisis, no citas inventadas del profesor. No hay libros/PDFs/ZIPs de spikes incorporados.

La cápsula histórica contiene dos saltos duros Markdown con espacios finales intencionales. `.gitattributes` admite esos espacios exclusivamente para ese archivo, para preservar su hash sin desactivar los controles de whitespace del resto.

## Fuentes reabiertas externamente el 2026-10-21 (no disponibles automáticamente en Git)

- PDF oficial `PDS - TI - Prof Pepe.pdf`: archivo recibido directamente en la conversación externa, SHA-256 **recalculado** `2222cf4ca087946f74a55ea786ca7773a91f42fb5e40c2900cf55b3b51688a49` idéntico al hash histórico. **No alojado en el repositorio**; REQUIREMENTS contiene la normalización.
- Materiales de clases, UML, DIP y Resort: adjuntos externos; no fueron todos reanalizados en esta reconciliación, y el lector de un clon no puede afirmar disponibilidad de sus originales. No redistribuir materiales sin permiso.
- Cápsula Schema 9 R6.0 externa: SHA-256 `2889e3f6c6d6d2163d9a9f0294d1542ac1821719b2f895d9b3cc8b4aa26c2985`; snapshot histórico, no autorización ni sustituto de Git.
- QA Windows PR #5: artefactos externos `report.md` SHA-256 `4720bfe8679b306a4c374b4cb7a964355c7c7a30c344e79beb2dfb6abf682fbc`, `result.json` SHA-256 `7500b808422caf9c5187ce18f1aa32b8070078e7ac2d30958a4a9f177b0ca27`, manifiesto `9327efa2ae9f7c66791e894545ee01f418eaaa2a3be27ad10c7190d5d7a077c7`. Los **48 archivos** NO se suben; sólo [resumen versionado](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Resultado de test/GUI/preservación `REPORTED_BY_AGENT`; hashes de adjuntos `VERIFIED`.
- GitHub consultado el 21/10: `main=ee83f9ebd30ee1b553c555c89d9ea453131d6678`, PR #5 `0670a4d5137f0e9f1db221866a02c29987024847`, OPEN/Draft no fusionado. Son lecturas fechadas: revalidar antes de actuar.

La tabla superior refleja **disponibilidad al bootstrap del 08/10**, no disponibilidad universal en todo chat; no atribuir acceso a bytes ausentes.
