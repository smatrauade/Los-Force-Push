# Punto 5 - Guia de instalacion en Los-Force-Push (GitHub Desktop / Windows)

**Alcance:** interfaz Swing, sprites pixel art 32x32, audio WAV original, controles,
menu, pausa, opciones, ayudas y vista de victoria preparada. No incluye implementacion
de hielo, cajas especiales, Undo, puntaje, guardado real ni progresion, que son tareas
separadas del equipo.

## 0. Antes de instalar

- Tener el repositorio **Los-Force-Push** abierto en GitHub Desktop y el JDK 17+
  disponible en Windows. El ZIP no requiere Maven, Gradle ni librerias Java extras.
- **No copies el ZIP de proyecto antiguo entero sobre tu clon de GitHub.** Este paquete
  trae unicamente los archivos del Punto 5 que se agregan o modifican.
- **NO copies .git** de otro lado. El ZIP de Punto 5 no contiene esa carpeta.
- Guardar/cerrar cambios propios sin commit o consultarlo con el equipo antes de
  actualizar el repositorio. Esta instalacion no debe pisar trabajo de otros.
- Pedir al equipo la rama base actualizada. El paquete fue preparado contra el ZIP
  aportado, no contra un checkout actualizado automaticamente de GitHub.

## 1. Crear la rama (GitHub Desktop)

1. Abrir **GitHub Desktop** y seleccionar el repositorio `Los-Force-Push`.
2. `Fetch origin` y luego `Pull origin` si hay actualizaciones y la carpeta esta limpia.
3. Desde `Current branch`, crear una nueva rama basada en la rama acordada:
   `feat/ui-pixel-art-ladrillo-cobre`.
4. En `Repository > Show in Explorer`, identificar la carpeta del clon. Ejemplo:
   `C:\Users\TU_USUARIO\Documents\GitHub\Los-Force-Push`.
5. Descomprimir `Los_Force_Push_Punto_5_Ladrillo_Cobre.zip` en una **carpeta distinta**
   del repositorio, por ejemplo `Descargas\Punto5`.

## 2. Instalar (modo recomendado, seguro)

Abrir PowerShell **dentro de la carpeta descomprimida**, donde se encuentra
`INSTALAR_EN_REPO.ps1`, no en el interior de `PARA_COPIAR_EN_TU_REPO`.

**Primero SIMULAR, no copia ningun archivo:**

```powershell
powershell -NoProfile -File .\INSTALAR_EN_REPO.ps1 -RepoPath "C:\Users\TU_USUARIO\Documents\GitHub\Los-Force-Push"
```

Si el resultado dice `READY`, instalar:

```powershell
powershell -NoProfile -File .\INSTALAR_EN_REPO.ps1 -RepoPath "C:\Users\TU_USUARIO\Documents\GitHub\Los-Force-Push" -Apply
```

**El instalador no hace commits, push, merge, cambios de rama ni cambios remotos.**
Comprueba integridad, existencia de `.git`, nombre del remoto, rama y colisiones
antes de copiar. Para archivos que reemplaza, exige que su contenido normalizado
corresponda a la base que enviaste; si otro companero los cambio, se detiene sin
sobrescribirlos. Si muestra `BLOCKED`/`COLLISION`, **no borres archivos ni fuerces**:
comparar las versiones nuevas en GitHub Desktop y reconciliar el codigo con el equipo.

**Alternativa manual:** copiar el contenido de `PARA_COPIAR_EN_TU_REPO` dentro de la
carpeta `Los-Force-Push` respetando sus subcarpetas `src`, `scripts`, `docs`. Solo si
no hay cambios concurrentes en `BoardPanel`, `GameController`, `GameView`,
`SwingApplication` y `scripts/run-swing.ps1`. No reemplazar a ciegas.

## 3. Probar desde el clon (Windows)

Abrir PowerShell **en la raiz de Los-Force-Push**:

```powershell
java -version
javac -version
powershell -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement
powershell -NoProfile -File .\scripts\run-swing.ps1 -Test
powershell -NoProfile -File .\scripts\run-swing.ps1 -UiTest
powershell -NoProfile -File .\scripts\run-swing.ps1
```

Si el JDK no esta en PATH, usar la variable admitida por los runners:

```powershell
$env:SPIKE_JDK_HOME = "C:\Program Files\Java\jdk-17"
powershell -NoProfile -File .\scripts\run-swing.ps1 -Test
```

Con `run-swing.ps1` sin argumento abre el **menu principal**. Desde ahi,
`NUEVA PARTIDA / DEMO` carga el `demo.txt` oficial. Para abrir directamente otro
nivel:

```powershell
powershell -NoProfile -File .\scripts\run-swing.ps1 -LevelPath "C:\ruta\nivel.txt"
```

Comprobar movimiento con flechas, caja sobre destino, cambio de escala, abrir TXT,
menu, pausa, audio y controles de reinicio preparados (inactivos hasta integrar Punto 4). **No exigir funcionalidad en botones
marcados pendientes**: Undo/Restart/guardar/cargar/progresion estan deshabilitados hasta
que se implementen sus modulos. La pantalla de victoria existe, pero aun no puede
ser invocada por un motor que no detecta victoria.

**Resultados de referencia en la copia de preparacion** (Linux, JDK 21 compilando a Java 17):
`40 PASS` carga, `31 PASS` movimiento, `16 PASS` escritorio,
`44 PASS` assets y `17 PASS` prueba de ventana real en Xvfb.
**No se ejecutaron estas pruebas en tu Windows**: repetilas en tu computadora.

## 4. Subir desde GitHub Desktop

1. Verificar `Changes`: nuevos `.png`, `.wav`, Java, documentacion y runners.
2. Revisar cada diferencia de **archivos existentes**; no subir archivos
   compilados, temporales, credenciales ni copias de `.git`.
3. Crear commit: `feat(ui): interfaz pixel art ladrillo y cobre con SFX`.
4. `Publish branch` o `Push origin` desde GitHub Desktop.
5. `Create Pull Request` hacia la rama acordada; pedir revision a otro integrante.
6. **No hacer merge directo a main** sin resolver antes colisiones e integraciones.

## 5. Lo que el equipo debe conectar despues

- Punto 1: ampliar el TXT para terreno especial y cajas especiales.
- Punto 2: exponer tipos de caja y durabilidad; adaptar el `SpriteResolver` de
  `BoardPanel` para representar el estado que entregue el modelo.
- Punto 3: llamar a `GameView.showVictory(LevelResult)` al finalizar, y conectar
  `SIGUIENTE NIVEL` a progresion; informar cerrojos y muros a la vista.
- Punto 4: conectar `Z  DESHACER` al Undo real de 5 pasos, hasta 3 usos, y revisar
  reinicio con la politica de sesion y metricas.
- Funcionalidad adicional: conectar `GUARDAR/CARGAR PARTIDA` al servicio elegido.

Ver `INTEGRACION_EQUIPO.md` para nombres, puntos de extension y limites reales.

## 6. Solucion de problemas

- **Faltan sprites / `getResourceAsStream` null:** usar el runner incluido, que agrega
  `src/desktop/resources` al classpath. En IntelliJ/Eclipse marcar esta carpeta como
  resource root (sin moverla) si lanzas la clase manualmente.
- **`javac` no encontrado:** instalar o configurar un JDK completo, no un JRE.
- **No se oye audio:** verificar `M`, las opciones de sonido y el dispositivo.
  El juego sigue funcionando cuando el audio no esta disponible.
- **Se ven pixeles borrosos:** el tablero usa nearest-neighbor; evitar zoom de
  Windows o escala fraccionaria fuera de la aplicacion, probar 1x/2x/3x.
- **La ventana no muestra demo:** usar `Abrir nivel TXT` y seleccionar
  `src\desktop\resources\levels\demo.txt`.
- **El instalador detecta colision:** tu rama avanzo desde el ZIP base;
  integrar manualmente tras comparar, nunca reemplazar trabajo de otro.
- **Windows bloquea scripts PowerShell:** seguir la politica de ejecucion de tu
  equipo o utilizar la copia manual; el paquete no cambia ExecutionPolicy global.
