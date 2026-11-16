[CmdletBinding()]
param([ValidateSet('Menu','TXT')][string]$Mode='Menu')
Set-StrictMode -Version 2.0
$ErrorActionPreference='Stop'
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$runner = Join-Path $root 'scripts\run-swing.ps1'
if (-not (Test-Path -LiteralPath $runner -PathType Leaf)) {
    throw 'FAIL_CLOSED: no se encuentra scripts/run-swing.ps1.'
}
$argsForRunner = @('-NoProfile','-ExecutionPolicy','Bypass','-File',$runner)
if ($Mode -eq 'TXT') {
    $level = Join-Path $root 'src\desktop\resources\levels\demo.txt'
    if (-not (Test-Path -LiteralPath $level -PathType Leaf)) {
        throw 'FAIL_CLOSED: falta el nivel demo.txt.'
    }
    $argsForRunner += @('-LevelPath',$level)
}
Write-Host "LFP_DEMO_MODE=$Mode"
& powershell.exe @argsForRunner
if ($LASTEXITCODE -ne 0) {
    throw "FAIL_CLOSED: run-swing.ps1 exit=$LASTEXITCODE."
}
Write-Host 'RESULT=DEMO_TERMINADA_PASS'
