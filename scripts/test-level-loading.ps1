# Windows PowerShell 5.1 / PowerShell 7; no shell emulation or build framework.
[CmdletBinding()]
param([switch]$IncludeMovement)

Set-StrictMode -Version 2.0
$ErrorActionPreference = 'Stop'
$runDirectory = $null
$created = $false
$exitCode = 1

try {
    $root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
    $jdkHome = $env:SPIKE_JDK_HOME
    if ([string]::IsNullOrEmpty($jdkHome)) { $jdkHome = $env:JAVA_HOME }
    $suffix = ''
    if ($env:OS -eq 'Windows_NT') { $suffix = '.exe' }
    if (-not [string]::IsNullOrEmpty($jdkHome)) {
        $compiler = Join-Path $jdkHome ('bin/javac' + $suffix)
        $runtime = Join-Path $jdkHome ('bin/java' + $suffix)
        if (-not (Test-Path -LiteralPath $compiler -PathType Leaf) -or
            -not (Test-Path -LiteralPath $runtime -PathType Leaf)) {
            throw 'A complete JDK is required at SPIKE_JDK_HOME or JAVA_HOME.'
        }
    } else {
        $compiler = (Get-Command ('javac' + $suffix) -CommandType Application).Source
        $runtime = (Get-Command ('java' + $suffix) -CommandType Application).Source
    }
    & $compiler -version
    if ($LASTEXITCODE -ne 0) { throw 'javac version check failed.' }
    & $runtime -version
    if ($LASTEXITCODE -ne 0) { throw 'java version check failed.' }

    $tempParent = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
    $rootPrefix = $root.TrimEnd([char[]]@('\', '/')) + [IO.Path]::DirectorySeparatorChar
    $tempPrefix = $tempParent.TrimEnd([char[]]@('\', '/')) + [IO.Path]::DirectorySeparatorChar
    if ($tempPrefix.StartsWith($rootPrefix, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Temporary directory must be outside the checkout.'
    }
    $runDirectory = Join-Path $tempParent ('sokoban-level-loading-' + [Guid]::NewGuid())
    # No -Force: a preexisting path is never reused or removed.
    New-Item -ItemType Directory -Path $runDirectory | Out-Null
    $created = $true
    $classes = Join-Path $runDirectory 'classes'
    New-Item -ItemType Directory -Path $classes | Out-Null
    # Compile only the experimental core. UI classes require java.desktop and
    # must not enter a deliberately java.base-only regression runner.
    # A product-wide build will be defined when OQ-11 is resolved.
    $coreDirectories = @(
        'src/main/java/ar/edu/uade/sokoban/level'
        'src/main/java/ar/edu/uade/sokoban/movement'
        'src/main/java/ar/edu/uade/sokoban/session'
        'src/test/java/ar/edu/uade/sokoban/level'
        'src/test/java/ar/edu/uade/sokoban/movement'
        'src/test/java/ar/edu/uade/sokoban/session'
    )
    $sources = @($coreDirectories | ForEach-Object {
        Get-ChildItem -LiteralPath (Join-Path $root $_) -Recurse -File -Filter '*.java'
    }) | Sort-Object FullName
    if (@($sources).Count -eq 0) { throw 'No Java sources found.' }
    $sourceList = Join-Path $runDirectory 'sources'
    # javac argfile: explicit UTF-8 without BOM, quoted paths and forward slashes.
    $sourceLines = [string[]]@($sources | ForEach-Object {
        '"' + $_.FullName.Replace('\', '/').Replace('"', '\"') + '"'
    })
    [IO.File]::WriteAllLines($sourceList, $sourceLines, (New-Object Text.UTF8Encoding($false)))
    $compileArgs = @('-J-Dfile.encoding=UTF-8', '--release', '17', '--limit-modules',
        'java.base', '-encoding', 'UTF-8', '-Xlint:all', '-Werror', '-d', $classes,
        ('@' + $sourceList))
    & $compiler @compileArgs
    if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
    $testClasses = @('ar.edu.uade.sokoban.level.LevelLoadingTest')
    if ($IncludeMovement) {
        $testClasses += @(
            'ar.edu.uade.sokoban.movement.MovementTest'
            'ar.edu.uade.sokoban.level.ExtendedLevelTest'
            'ar.edu.uade.sokoban.movement.DynamicWorldTest'
            'ar.edu.uade.sokoban.session.CompletionTest'
        )
    }
    foreach ($testClass in $testClasses) {
        $testArgs = @('--limit-modules', 'java.base', ('-Djava.io.tmpdir=' + $runDirectory),
            '-cp', $classes, $testClass, (Join-Path $root 'src/test/resources/levels'))
        & $runtime @testArgs
        if ($LASTEXITCODE -ne 0) { throw ('Tests failed: ' + $testClass) }
    }
    $exitCode = 0
} catch {
    Write-Error -Message $_.Exception.Message -ErrorAction Continue
} finally {
    if ($created) {
        try {
            Remove-Item -LiteralPath $runDirectory -Recurse -Force
        } catch {
            Write-Error -Message ('Temporary cleanup failed: ' + $_.Exception.Message) -ErrorAction Continue
            $exitCode = 1
        }
    }
}
exit $exitCode
