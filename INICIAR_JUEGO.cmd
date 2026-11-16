@echo off
setlocal EnableExtensions DisableDelayedExpansion
if not exist "%~dp0scripts\demo\start-demo.ps1" (
  echo ERROR: falta el lanzador scripts\demo\start-demo.ps1
  pause
  exit /b 1
)
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\demo\start-demo.ps1" -Mode Menu
set "result=%ERRORLEVEL%"
if not "%result%"=="0" (
  echo ERROR: no se pudo iniciar el juego. Revisar JDK 17+ y archivos del repositorio.
  pause
)
exit /b %result%
