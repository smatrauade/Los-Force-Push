# Native Windows CLI regression; requires the same complete JDK as the runner.
[CmdletBinding()]
param()

Set-StrictMode -Version 2.0
$ErrorActionPreference = 'Stop'
$owned = $null
$created = $false
$exitCode = 1
$passed = 0
$failed = 0

function Invoke-Runner([string]$Shell, [string[]]$RunnerArguments, [string]$Temp) {
    $start = New-Object Diagnostics.ProcessStartInfo
    $start.FileName = $Shell
    # All tokens are controlled below; only the script path can contain spaces.
    $tokens = @('-NoProfile', '-File', $script:runner) + $RunnerArguments
    $start.Arguments = ($tokens | ForEach-Object { '"' + $_ + '"' }) -join ' '
    $start.UseShellExecute = $false
    $start.CreateNoWindow = $true
    $start.RedirectStandardOutput = $true
    $start.RedirectStandardError = $true
    $start.EnvironmentVariables['TEMP'] = $Temp
    $start.EnvironmentVariables['TMP'] = $Temp
    $process = New-Object Diagnostics.Process
    $process.StartInfo = $start
    try {
        if (-not $process.Start()) { throw 'Cannot start runner subprocess.' }
        $stdout = $process.StandardOutput.ReadToEndAsync()
        $stderr = $process.StandardError.ReadToEndAsync()
        if (-not $process.WaitForExit(120000)) {
            $process.Kill()
            $process.WaitForExit()
            throw 'Runner subprocess timed out.'
        }
        return [pscustomobject]@{
            ExitCode = $process.ExitCode
            Stdout = $stdout.GetAwaiter().GetResult()
            Stderr = $stderr.GetAwaiter().GetResult()
        }
    } finally {
        $process.Dispose()
    }
}

try {
    if ($env:OS -ne 'Windows_NT') { throw 'This regression requires native Windows.' }
    $root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
    $script:runner = Join-Path $PSScriptRoot 'test-level-loading.ps1'
    if (-not (Test-Path -LiteralPath $script:runner -PathType Leaf)) {
        throw 'The real test runner is missing.'
    }
    $shells = @()
    $windowsPowerShell = Join-Path $env:SystemRoot 'System32/WindowsPowerShell/v1.0/powershell.exe'
    if (Test-Path -LiteralPath $windowsPowerShell -PathType Leaf) {
        $shells += [pscustomobject]@{ Name = 'WindowsPowerShell'; Path = $windowsPowerShell }
    } else {
        Write-Output 'CLI SHELL WindowsPowerShell NOT_RUN_NOT_INSTALLED'
    }
    $pwsh = Get-Command 'pwsh.exe' -CommandType Application -ErrorAction SilentlyContinue
    if ($null -ne $pwsh) {
        $shells += [pscustomobject]@{ Name = 'PowerShell7'; Path = $pwsh.Source }
    } else {
        Write-Output 'CLI SHELL PowerShell7 NOT_RUN_NOT_INSTALLED'
    }
    if ($shells.Count -eq 0) { throw 'No native PowerShell executable found.' }

    $tempParent = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
    $rootPrefix = $root.TrimEnd([char[]]@('\', '/')) + [IO.Path]::DirectorySeparatorChar
    $tempPrefix = $tempParent.TrimEnd([char[]]@('\', '/')) + [IO.Path]::DirectorySeparatorChar
    if ($tempPrefix.StartsWith($rootPrefix, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Regression temporaries must be outside the checkout.'
    }
    $owned = Join-Path $tempParent ('sokoban-runner-arguments-' + [Guid]::NewGuid())
    New-Item -ItemType Directory -Path $owned | Out-Null
    $created = $true
    $cases = @(
        @{ Name = 'legacy'; Arguments = @(); Movement = $false; InvalidToken = '' },
        @{ Name = 'movement'; Arguments = @('-IncludeMovement'); Movement = $true; InvalidToken = '' },
        @{ Name = 'case-insensitive'; Arguments = @('-includemovement'); Movement = $true; InvalidToken = '' },
        @{ Name = 'abbreviation'; Arguments = @('-IncludeMov'); Movement = $true; InvalidToken = '' },
        @{ Name = 'typo'; Arguments = @('-IncludeMovment'); Movement = $false; InvalidToken = 'IncludeMovment' },
        @{ Name = 'unknown'; Arguments = @('-UnknownOption'); Movement = $false; InvalidToken = 'UnknownOption' },
        @{ Name = 'positional'; Arguments = @('RUNNER_CLI_UNEXPECTED'); Movement = $false; InvalidToken = 'RUNNER_CLI_UNEXPECTED' },
        @{ Name = 'trailing'; Arguments = @('-IncludeMovement', 'RUNNER_CLI_UNEXPECTED'); Movement = $false; InvalidToken = 'RUNNER_CLI_UNEXPECTED' },
        @{ Name = 'mixed'; Arguments = @('-IncludeMovement', '-UnknownOption'); Movement = $false; InvalidToken = 'UnknownOption' }
    )
    $sequence = 0
    foreach ($shell in $shells) {
        $shellPassed = 0
        $shellFailed = 0
        foreach ($case in $cases) {
            $sequence++
            $temp = Join-Path $owned ([string]$sequence)
            New-Item -ItemType Directory -Path $temp | Out-Null
            $result = Invoke-Runner $shell.Path $case.Arguments $temp
            $loading = [regex]::Matches($result.Stdout, '(?m)^PASS \d{2} ').Count
            $movement = [regex]::Matches($result.Stdout, '(?m)^MOVEMENT PASS \d{2} ').Count
            $javaStarted = ($result.Stdout + $result.Stderr) -match '(?m)^(javac |openjdk version |java version )'
            $summary = $result.Stdout -match '(?m)^(MOVEMENT )?RESULT:'
            $residues = @(Get-ChildItem -LiteralPath $temp -Force)
            $cleanup = $true
            foreach ($residue in $residues) {
                # HotSpot can leave its empty performance-data directory after exit.
                if (-not $residue.PSIsContainer -or $residue.Name -notlike 'hsperfdata_*' -or
                    @(Get-ChildItem -LiteralPath $residue.FullName -Force).Count -ne 0) {
                    $cleanup = $false
                }
            }
            $diagnostic = $true
            if ($case.InvalidToken.Length -ne 0) {
                # Require the offending token in the error, not a missing-JDK failure.
                $diagnostic = $result.Stderr.Contains($case.InvalidToken)
                $ok = $result.ExitCode -ne 0 -and $diagnostic -and -not $javaStarted -and
                    $loading -eq 0 -and $movement -eq 0 -and -not $summary -and $residues.Count -eq 0
            } else {
                $expectedMovement = 0
                if ($case.Movement) { $expectedMovement = 31 }
                $ok = $result.ExitCode -eq 0 -and $javaStarted -and $loading -eq 40 -and
                    $movement -eq $expectedMovement -and $cleanup -and
                    $result.Stdout -match '(?m)^RESULT: 40 PASS, 0 FAIL\r?$' -and
                    $result.Stdout -notmatch '(?m)^(MOVEMENT )?FAIL '
                if ($case.Movement) {
                    $ok = $ok -and $result.Stdout -match '(?m)^MOVEMENT RESULT: 31 PASS, 0 FAIL\r?$'
                } else {
                    $ok = $ok -and $result.Stdout -notmatch '(?m)^MOVEMENT '
                }
            }
            $status = 'FAIL'
            if ($ok) {
                $status = 'PASS'
                $passed++
                $shellPassed++
            } else {
                $failed++
                $shellFailed++
            }
            Write-Output ('CLI {0} shell={1} case={2} exit={3} loading={4} movement={5} java={6} diagnostic={7} cleanup={8}' -f
                $status, $shell.Name, $case.Name, $result.ExitCode, $loading, $movement,
                $javaStarted, $diagnostic, $cleanup)
            if ($case.InvalidToken.Length -ne 0 -or -not $ok) {
                Write-Output ('CLI STDERR shell={0} case={1}: {2}' -f $shell.Name, $case.Name, $result.Stderr.Trim())
            }
            if (-not $ok) { Write-Output $result.Stdout }
        }
        Write-Output ('CLI SHELL {0}: {1} PASS, {2} FAIL' -f $shell.Name, $shellPassed, $shellFailed)
    }
    Write-Output ('CLI RESULT: {0} PASS, {1} FAIL' -f $passed, $failed)
    if ($failed -eq 0) { $exitCode = 0 }
} catch {
    Write-Error -Message $_.Exception.Message -ErrorAction Continue
} finally {
    if ($created) {
        try {
            Remove-Item -LiteralPath $owned -Recurse -Force
        } catch {
            Write-Error -Message ('Regression cleanup failed: ' + $_.Exception.Message) -ErrorAction Continue
            $exitCode = 1
        }
    }
}
exit $exitCode
