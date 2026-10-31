# Fast negative checks for the PowerShell Swing entrypoint (Windows 5.1/7).
[CmdletBinding()]
param()
Set-StrictMode -Version 2.0
$ErrorActionPreference = 'Continue'
$runner = Join-Path $PSScriptRoot 'run-swing.ps1'
$shell = (Get-Process -Id $PID).Path
$missing = Join-Path ([IO.Path]::GetTempPath()) ('sokoban-missing-' + [Guid]::NewGuid() + '.txt')
$cases = @(
    @{ Name = 'extended-without-path'; Arguments = @('-Extended'); Diagnostic = '-Extended requires -LevelPath.' },
    @{ Name = 'both-tests'; Arguments = @('-Test', '-UiTest'); Diagnostic = 'cannot be combined' },
    @{ Name = 'test-extended'; Arguments = @('-Test', '-Extended'); Diagnostic = 'modes cannot be combined' },
    @{ Name = 'ui-test-path'; Arguments = @('-UiTest', '-LevelPath', 'unused.txt'); Diagnostic = 'modes cannot be combined' },
    @{ Name = 'missing-file'; Arguments = @('-Extended', '-LevelPath', $missing); Diagnostic = 'file does not exist.' }
)
$failed = 0
foreach ($case in $cases) {
    $arguments = $case.Arguments
    $result = (& $shell -NoProfile -File $runner @arguments 2>&1 | Out-String)
    $code = $LASTEXITCODE
    $ok = $code -ne 0 -and $result.Contains($case.Diagnostic)
    if (-not $ok) { $failed++; Write-Output $result }
    Write-Output ('SWING ARGS {0} case={1} exit={2}' -f $(if ($ok) { 'PASS' } else { 'FAIL' }), $case.Name, $code)
}
Write-Output ('SWING ARGS RESULT: {0} PASS, {1} FAIL' -f ($cases.Count - $failed), $failed)
if ($failed -ne 0) { exit 1 }
