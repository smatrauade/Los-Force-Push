# Demostración de Sokoban: accesos sin comandos manuales

Requisitos: **JDK completo 17+** con `java` y `javac` disponibles; en Windows, PowerShell incluido en el sistema; en Linux, Bash y un escritorio gráfico. No se necesitan Maven, Gradle ni conexión a Internet después de clonar.

## Windows (Explorador de archivos)

1. Hacer doble clic en [`INICIAR_JUEGO.cmd`](../../INICIAR_JUEGO.cmd) para iniciar el menú del juego.
2. Para abrir **directamente** el nivel TXT de demostración, hacer doble clic en [`DEMO_CARGAR_TXT.cmd`](../../DEMO_CARGAR_TXT.cmd). Se carga `src/desktop/resources/levels/demo.txt` mediante el parámetro público del juego.
3. Para **mostrar el selector gráfico** de archivos, abrir el juego con `INICIAR_JUEGO.cmd`, presionar **ABRIR NIVEL TXT** y elegir `src/desktop/resources/levels/demo.txt`. Esto permite explicar el proceso real de validación sin código ni consola.

Los `.cmd` llaman a los lanzadores PowerShell con política `Bypass` **únicamente para ese proceso**, sin cambiar la configuración global. Los `.ps1` usan el runner ya existente, compilan en una carpeta temporal y ejecutan la aplicación. Ante un error, la ventana de consola conserva el diagnóstico.

## Linux (Bash)

Desde la raíz del repositorio, con una ruta **sin espacios**, ejecutar una sola línea:

```bash
bash INICIAR_JUEGO.sh
```

Para comenzar en el nivel TXT:

```bash
bash DEMO_CARGAR_TXT.sh
```

Los dos archivos también pueden abrirse mediante el gestor de archivos cuando está configurado para **ejecutar scripts**, no para editarlos; eso depende del escritorio y de la confianza otorgada localmente. No se modifican configuraciones del sistema para forzar doble clic.

## Hoja rápida del operador

Las soluciones de los cinco niveles están en la [tarjeta del operador](../../entregables/TARJETA_OPERADOR_DEMO_18_MIN.txt) y el [guion](../../entregables/GUION_DEFENSA_5_INTEGRANTES.md). Resumen, desde **NUEVA PARTIDA / DEMO**:

| Nivel | Teclas | Score sin Undo |
|---|---|---:|
| demo | `→ → →` | 993 |
| campaign-b | `→ ← ↓ ↓ → → ↑ → ↑ ← ←` | 983 |
| ice_demo | `↓ →` | 996 |
| fragile_demo | `→ → →` | 991 |
| fragile-ice-demo | `→ ↓ → → → → →` | 989 |

**Esperar cada animación**. Undo requiere cinco movimientos exitosos; después de una victoria, Replay queda habilitado si la traza es válida. No usar el lanzador TXT a mitad de la campaña si se necesita mantener su progreso: abrir un nivel externo inicia una sesión nueva.

## Antes de la exposición

- Probar la presentación en el equipo y proyector definitivos.
- Probar ambos lanzadores y verificar JDK, sprites, sonido y foco de teclado.
- Dejar abierta la presentación y una partida preparada si el tiempo es estricto.
- Usar [`VIDEO_GAMEPLAY.mp4`](../../entregables/VIDEO_GAMEPLAY.mp4) sólo como respaldo, sin confundir reproducción con demo en vivo.
- Para PowerPoint y PDF: [presentación editable](../../entregables/Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx) y [copia PDF](../../entregables/Los_Force_Push_Sokoban_Defensa_18-11-2026.pdf).

Las rutas son relativas al repositorio y funcionan después de clonarlo. No se incluyen scripts que automaticen pulsaciones sobre la UI ni se modifica el juego.
