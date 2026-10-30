# Registro de fuentes y disponibilidad

## Disponibilidad integrada — 2026-10-28

PR #9 y #8 están fusionados según Git/GitHub; [CURRENT_STATE](CURRENT_STATE.md) registra los commits de merge. El clon contiene las fuentes Java, tests, runners, 31 PNG y 12 WAV del Punto 5. El [manifiesto canónico](../punto5/ASSETS_REGISTRO_SHA256.csv) identifica sus 43 rutas/hashes/orígenes; no duplicar su tabla. Hay 29 sprites base y dos variantes opcionales. La [declaración de IA/derechos](../punto5/USO_IA_Y_LICENCIAS.md) mantiene la validación CC0/titularidad pendiente del equipo.

La auditoría SOKOBAN_POST_PR9_INTEGRATION_R1 del 28/10 ejecutó QA combinado sobre el árbol 1796932307df319e3636daee942cf49920b4eb05 antes del merge real de #8. Fuente: ejecucion de auditoria por Codex del 28/10, registrada en la entrega R1; comandos/resultados y limites se normalizan en [TEST_STRATEGY](../testing/TEST_STRATEGY.md). No se incorporan logs/capturas externos ni se presenta esa QA como una nueva ejecución funcional del mantenimiento R2. La integridad de los archivos presentes puede volver a comprobarse desde el clon.

Las tablas y secciones fechadas que siguen conservan disponibilidad/procedencia histórica; «assets no disponibles» se refiere al bootstrap, no al árbol integrado actual. Los originales académicos/privados ausentes siguen ausentes. No se modifica la autoridad de las encuestas ni se inventan licencias.

Revisado 2026-10-23 para anexar la procedencia de encuestas. La tabla conserva la disponibilidad al bootstrap del 08/10. Prioridad: consigna → aclaraciones posteriores → oral → cátedra → equipo → bibliografía → GitHub → inferencia. No mezclar rango de autoridad con disponibilidad de bytes.

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

La fecha de la cápsula y sus declaraciones de no autorización reflejan la tarea anterior. En la integración histórica del 08/10 prevalecía la solicitud de bootstrap de esa fecha. Las conclusiones nuevas se etiquetan como análisis, no citas inventadas del profesor. No hay libros/PDFs/ZIPs de spikes incorporados.

La cápsula histórica contiene dos saltos duros Markdown con espacios finales intencionales. `.gitattributes` admite esos espacios exclusivamente para ese archivo, para preservar su hash sin desactivar los controles de whitespace del resto.

## Fuentes reabiertas externamente el 2026-10-21 (no disponibles automáticamente en Git)

- PDF oficial `PDS - TI - Prof Pepe.pdf`: archivo recibido directamente en la conversación externa, SHA-256 **recalculado** `2222cf4ca087946f74a55ea786ca7773a91f42fb5e40c2900cf55b3b51688a49` idéntico al hash histórico. **No alojado en el repositorio**; REQUIREMENTS contiene la normalización.
- Materiales de clases, UML, DIP y Resort: adjuntos externos; no fueron todos reanalizados en esta reconciliación, y el lector de un clon no puede afirmar disponibilidad de sus originales. No redistribuir materiales sin permiso.
- Cápsula Schema 9 R6.0 externa: SHA-256 `2889e3f6c6d6d2163d9a9f0294d1542ac1821719b2f895d9b3cc8b4aa26c2985`; snapshot histórico, no autorización ni sustituto de Git.
- QA Windows PR #5: artefactos externos `report.md` SHA-256 `4720bfe8679b306a4c374b4cb7a964355c7c7a30c344e79beb2dfb6abf682fbc`, `result.json` SHA-256 `7500b808422caf9c5187ce18f1aa32b8070078e7ac2d30958a4a9f177b0ca27`, manifiesto `9327efa2ae9f7c66791e894545ee01f418eaaa2a3be27ad10c7190d5d7a077c7`. Los **48 archivos** NO se suben; sólo [resumen versionado](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Resultado de test/GUI/preservación `REPORTED_BY_AGENT`; hashes de adjuntos `VERIFIED`.
- GitHub consultado el 21/10: `main=ee83f9ebd30ee1b553c555c89d9ea453131d6678`, PR #5 `0670a4d5137f0e9f1db221866a02c29987024847`, OPEN/Draft no fusionado. Son lecturas fechadas: revalidar antes de actuar.

La tabla superior refleja **disponibilidad al bootstrap del 08/10**, no disponibilidad universal en todo chat; no atribuir acceso a bytes ausentes.

## Encuestas del equipo — 2026-10-23

```text
SOURCE = USER_SUPPLIED_SCREENSHOT_TRANSCRIPTION
RAW_SCREENSHOTS = EXTERNAL_PRIVATE
RAW_VERIFICATION = NOT_PERFORMED_BY_THIS_AGENT
```

El solicitante aportó una transcripción revisada de once capturas de detalles de encuesta del 23/10 y confirmó el método de mayoría absoluta del equipo. La misión documental del 23/10 leyó esa transcripción, **no** las capturas originales ni el ZIP de WhatsApp. La exportación mencionada contiene preguntas/discusiones pero no votos; no atribuirle los recuentos ni afirmar verificación independiente de imágenes no disponibles.

Sólo los agregados anónimos se incorporan a [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) mantiene el alcance y los pendientes. El antecedente de transcripción E04, la anomalía E05 y la selección múltiple E11 se conservan allí. La corrección posterior de E04 se registra más abajo; su antigua deducción de doble selección no es dato vigente. No se publican originales, datos personales ni hashes de capturas cuyos bytes no se tienen. La fuente TEAM no sustituye el enunciado ni las aclaraciones documentadas de cátedra; no es una prueba del juego, del diseño ni del código. El PDF original no se releyó en esta misión.

## Fuentes académicas aportadas — 29/10/2026

Reconciliación basada en la solicitud del usuario y su resumen de misión, leídos en esta ejecución. No hubo acceso directo a Teams, PDF oficial, corpus de cátedra, transcripciones completas ni audio. No se redistribuyen esos originales. Los hashes reportados en fuentes externas no se han recalculado aquí. La autoridad de la consigna sigue OFFICIAL según su normalización; no se convierte ASR en texto oficial.

| Fuente y ubicación temporal | Autoridad / fidelidad | Afirmación e impacto documental |
|---|---|---|
| Teams, texto aportado el 29/10 | TEACHER-EXPLICIT / USER_SUPPLIED_TEAMS_TEXT; mensaje original externo | 18/11, demo 15–20 min, entrega por correo; REQUIREMENTS y TODO |
| 23/09 03:00:50–03:02:09; 30/09 01:07:56–01:08:51 | Indicación oral / USER_SUPPLIED_TRANSCRIPT_SUMMARY / AUDIO_NOT_VERIFIED | Alfabeto del equipo; TXT propio o editado por docente, carga real; RF-02 y TEST_STRATEGY |
| 30/09 00:34:18–00:35:47; 07/10 00:31:47–00:32:04 | Misma procedencia ASR | Builder como ejemplo opcional, alternativa justificada; D-011/D-012 |
| 23/09 03:05:27–03:05:53 | Misma procedencia ASR | Niveles del equipo resolubles y resistencia coherente con puzzle; aceptación, sin máximo oficial |
| 23/09 03:07:10–03:07:23; 21/10 00:15:01–00:16:24 y 00:04:17–00:04:21 | Misma procedencia ASR; indicación más fuerte que recomendación | Observer en alguna parte del TPO; ejemplo Lock notifica sólo a muros suscriptos; D-010, pendiente de implementación |
| 23/09 03:07:31–03:08:23; 21/10 02:42–03:04 | Misma procedencia ASR; timestamp corto conservado sin completar | Command para acciones frente a Memento para estados; alternativas de Undo, no exigir ambos |
| 23/09 03:08:39–03:09:08 | Misma procedencia ASR | Strategy candidato para cálculo de score; fórmula pendiente |
| 14/10 03:25–03:27 | Misma procedencia ASR; sin completar precisión | Factory/State/Strategy y posible power-up Decorator; no selección TEAM de segunda adicional |
| Resumen docente aportado, sin timestamp adicional verificado | Misma procedencia ASR | Prototype sin justificación actual; Singleton/audio no obligatorio; MVC separa reglas, coordinación y vista |

La investigación externa de solver/deadlocks/generación no es requisito docente ni se revisó en esta ejecución: NOT_APPLICABLE a la ruta crítica. E01–E11 permanecen únicamente en DECISION_LOG; esta tabla registra fuentes, no votos ni nuevas decisiones.

## Contexto grupal privado — complemento 29/10/2026

Fuente leída en esta misión: resumen impersonal aportado por el usuario de una exportación de WhatsApp generada el 29/10/2026 a las 11:52, con mensajes del 30/09 al 29/10. No se leyeron aquí los bytes de la exportación ni respuestas docentes originales. Hay voces, multimedia y resultados de encuestas omitidos: no reconstruirlos ni publicar mensajes completos, nombres, teléfonos, identificadores o capturas.

| Fecha / alcance reportado | Clasificación | Uso y límite |
|---|---|---|
| 28/10, aproximadamente 17:19–17:22 | TEAM_MESSAGE_REPORTING_TEACHER / SECONDHAND / ORIGINAL_TEACHER_RESPONSE_NOT_VERIFIED | Un mensaje atribuye al profesor que guardar no cuenta como adicional, lo relaciona con Undo/Memento y reporta elegibilidad del selector de estilos; otro expresa conformidad. No constituye respuesta docente directa ni ratificación por mayoría |
| 27/10 encuesta/propuesta visual; 28/10 reporte posterior | TEAM_PROPOSAL / TEACHER_REPORTED_ELIGIBILITY / IMPLEMENTATION_NOT_VERIFIED | Selector de estilos/sprites candidato; resultados completos no disponibles, patrón e implementación por demostrar |
| 24/10 organización | TEAM_PROPOSAL | Cinco áreas propuestas: TXT/niveles, movimiento/cajas especiales, cerrojos/victoria, Undo/Restart/puntaje e interfaz/recursos. Hubo ofrecimiento para la cuarta y propuesta de Issues; no asignación nominal completa |
| 29/10 patrones | TEAM_PROPOSAL | Factory Method y Abstract Factory sugeridos para entidades/niveles; no acuerdos ratificados |
| Fechas discutidas | SECONDHAND / INCOMPLETE_CONTEXT | Parcial sin fecha exacta; pregunta sobre 1/11 con respuestas de voz omitidas, no deadline confirmado. Teams conserva entrega oficial del 18/11/2026 |

GitHub consultado en esta misión: el endpoint de Issues (abiertos y cerrados, excluidos PR) devolvió lista vacía; no constan Issues creados en esa consulta. No se crearon ni asignaron tareas. La evidencia TEAM indirecta no se eleva a TEACHER-EXPLICIT ni sustituye la consigna escrita. [Compatibilidad de E11](../decisions/DECISION_LOG.md#compatibilidad-académica-de-e11--complemento-29102026) y [OQ-10](../requirements/OPEN_QUESTIONS.md#elegibilidad-de-adicionales--complemento-29102026) delimitan la incertidumbre.

## Evidencia técnica local TXT — 29/10/2026

Solicitud del hito filesystem del 29/10/2026: desarrollo TXT y publicación en la rama existente, con limpieza Git independiente; aquella autorización no incluía PR ni merge. El documento externo de gestión de worktrees se leyó como contexto operativo, sin heredar órdenes embebidas ni ampliar el alcance a todas las ramas.

Baseline G0–G1: ejecución local aportada por el usuario, identificador externo `run.vNBarLPb`, código `8b71712cec673651b8dffe792bdc4ee7c779c1cf`. Se leyeron metadata, resumen, cuatro logs completos, mapa F01–F14 y script R2 aportado. El script comprueba salida cero del compilador y de cada suite antes de generar el resumen; no guarda un campo numérico de exit por comando. Los logs contienen los casos 40/32/31/31 sin fallos y javac.log vacío. HEAD/worktree sin cambios funcionales posteriores al SHA ensayado al aceptar el baseline. `PASS_REPORTED_AND_VERIFIED`, sin atribuir una nueva ejecución a Codex ni inventar códigos registrados por separado.

Incremento filesystem: lectura directa del código/tests del clon y ejecuciones nuevas por Codex; resultados, contenido identificable y reproducción en [TEST_STRATEGY](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). Sin nueva fuente normativa, libro, código de terceros ni licencia adoptada. Asistencia de IA declarada; revisión/aceptación del equipo pendiente. Los logs locales originales son externos; los comandos y el conocimiento técnico necesarios permanecen en Git.

## Coordinación TXT y Fragile/hielo — 29/10/2026

`SOURCE = USER_SUPPLIED_TEAM_COORDINATION_SUMMARY`; `AUTHORITY = TEAM_COORDINATION`; `ORIGINAL_MESSAGES = NOT_INSPECTED`. El usuario aportó un resumen de una conversación directa entre integrantes. Resultado técnico: un integrante reportó trabajo en Fragile/hielo y ofreció preparar un nivel de prueba para facilitar la integración. El ofrecimiento y la actividad reportada no demuestran implementación terminada, entrega de archivos ni compatibilidad. La estructura/cantidad de niveles de demo no quedó acordada.

Se registra sólo esta consecuencia para evitar colisiones entre dominios: TXT documenta definición/validación; el consumidor desarrolla sus mecánicas y acuerda la frontera. No es votación, ratificación de gramática, respuesta docente ni asignación de otras áreas. No se inspeccionaron ramas del compañero ni se enviaron mensajes. Preguntas y datos disponibles en el [traspaso técnico](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026). Sin nombres, transcripciones ni identificadores personales.

## Solicitud de promoción TXT — 29/10/2026

Fuente leída: prompt de cierre aportado y expresamente invocado por el usuario. Autoriza un único PR Draft mediante la sesión local Git/gh y squash sólo tras revisión humana admisible y gates técnicos. Supersede la restricción operativa anterior de no abrir PR; no cambia consigna, E01/E02, OQ ni aceptación del equipo. El texto externo no es una dependencia para compilar/probar: los comandos están en TEST_STRATEGY. No se publica el adjunto privado.

## Cierre semántico TXT y corrección E04 — 29/10/2026

Fuente directa: solicitud actual del responsable TXT/niveles y prompt adjunto expresamente invocado. Se normalizan únicamente sus consecuencias técnicas; no se publica el adjunto, capturas, chats ni datos personales. Autoridad delegada `MODULE_OWNER_DECISION`, subordinada a consigna, aclaraciones docentes y decisiones TEAM; no es una votación ni revisión grupal de código.

| Afirmación | Fuente / autoridad | Elección / implementación | Destino canónico |
|---|---|---|---|
| E04 conserva ganador y corrige recuento | `USER_DIRECT_CORRECTION_2026-10-29 / ORIGINAL_SCREENSHOTS_NOT_REVERIFIED`; testimonio directo del informante sobre encuesta TEAM | TEAM_DECIDED; implementación en otro módulo NOT_IMPLEMENTED en este árbol | DECISION_LOG E04 y OQ-03 |
| Esquema Fragile actual adoptado | Delegación expresa al responsable TXT; `MODULE_OWNER_DECISION`, no TEAM_MAJORITY | MODULE_OWNER_DECIDED; definición IMPLEMENTED, gameplay NOT_IMPLEMENTED | D-020, contrato y OQ-06 |
| Subconjunto C2/Fragile formalizado | Misma autoridad, respaldada por código/tests leídos | MODULE_OWNER_DECIDED parcial; extensiones OPEN_DEPENDENCY | D-020, contrato y OQ-08 |
| E08 ya eligió reinicio del límite | Encuesta TEAM histórica preservada; solicitud directa aclara que no debe reabrirse | TEAM_DECIDED; implementación/bordes en sesión/Undo | E08 y OQ-04 |

El recuento vigente E04 se atribuye a esa corrección, no a auditoría visual. El registro anterior `e9b2b2a` transcribía 4/2 y deducía seis selecciones/una doble selección; ese antecedente queda HISTORICAL / SUPERSEDED en el registro canónico. No se infieren identidades ni número exacto de personas con votos múltiples a partir del recuento corregido. La exportación histórica contiene preguntas, no votos. No se inspeccionaron capturas originales, audio ni PDF oficial de nuevo. E05/E11 y sus recuentos no se alteran.

La consigna normalizada conserva rotura al cero; la secuenciación particular E04 es elección operativa TEAM, sin aprobación docente independiente atribuida. Se corrige un bloqueo inferido, no una fuente oficial. No hay nueva evidencia de gameplay, demo terminada ni aceptación del equipo. El hito autoriza un commit documental y actualización del PR #15 existente, conservándolo Draft; no merge ni Ready. No hereda autorización de squash de solicitudes anteriores.
