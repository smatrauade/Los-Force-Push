# Experimental Swing slice; PowerShell 5.1/7 and an existing complete JDK.
[CmdletBinding()]
param(
    [string]$LevelPath,
    [switch]$Extended,
    [switch]$Test,
    [switch]$UiTest
)
Set-StrictMode -Version 2.0
$ErrorActionPreference = 'Stop'
$runDirectory = $null
$created = $false
$exitCode = 1
try {
    if ($Test -and $UiTest) { throw '-Test and -UiTest cannot be combined.' }
    if (($Test -or $UiTest) -and ($Extended -or -not [string]::IsNullOrWhiteSpace($LevelPath))) {
        throw 'Test modes cannot be combined with -LevelPath or -Extended.'
    }
    if ($Extended -and [string]::IsNullOrWhiteSpace($LevelPath)) { throw '-Extended requires -LevelPath.' }
    $root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
    $explicitLevel = -not [string]::IsNullOrEmpty($LevelPath)
    if ($explicitLevel) {
        # Relative input is interpreted against the caller's working directory.
        $LevelPath = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($LevelPath)
        if (-not (Test-Path -LiteralPath $LevelPath -PathType Leaf)) { throw 'Level file does not exist.' }
    }
    $jdkHome = $env:SPIKE_JDK_HOME
    if ([string]::IsNullOrEmpty($jdkHome)) { $jdkHome = $env:JAVA_HOME }
    if (-not [string]::IsNullOrEmpty($jdkHome)) {
        $compiler = Join-Path $jdkHome 'bin/javac.exe'
        $runtime = Join-Path $jdkHome 'bin/java.exe'
        if (-not (Test-Path -LiteralPath $compiler -PathType Leaf) -or
            -not (Test-Path -LiteralPath $runtime -PathType Leaf)) {
            throw 'A complete JDK is required at SPIKE_JDK_HOME or JAVA_HOME.'
        }
    } else {
        $compiler = (Get-Command javac.exe -CommandType Application).Source
        $runtime = (Get-Command java.exe -CommandType Application).Source
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
    $runDirectory = Join-Path $tempParent ('sokoban-swing-' + [Guid]::NewGuid())
    New-Item -ItemType Directory -Path $runDirectory | Out-Null
    $created = $true
    $classes = Join-Path $runDirectory 'classes'
    New-Item -ItemType Directory -Path $classes | Out-Null
    $sourceRoots = @('src/main/java', 'src/desktop/java')
    if ($Test -or $UiTest) { $sourceRoots += 'src/desktop-test/java' }
    $sources = @($sourceRoots | ForEach-Object {
        Get-ChildItem -LiteralPath (Join-Path $root $_) -Recurse -File -Filter '*.java'
    }) | Sort-Object FullName
    $sourceList = Join-Path $runDirectory 'sources'
    $sourceLines = [string[]]@($sources | ForEach-Object {
        '"' + $_.FullName.Replace('\', '/').Replace('"', '\"') + '"'
    })
    [IO.File]::WriteAllLines($sourceList, $sourceLines, (New-Object Text.UTF8Encoding($false)))
    & $compiler '-J-Dfile.encoding=UTF-8' --release 17 --limit-modules java.base,java.desktop -encoding UTF-8 -Xlint:all -Werror -d $classes ('@' + $sourceList)
    if ($LASTEXITCODE -ne 0) { throw 'Swing compilation failed.' }
    # Java resources stay on the classpath (sprites + WAVs); no copies in source tree.
    $resources = Join-Path $root 'src/desktop/resources'
    $classPath = $classes + [IO.Path]::PathSeparator + $resources
    $javaArgs = @('-Dfile.encoding=UTF-8', '--limit-modules', 'java.base,java.desktop',
        ('-Djava.io.tmpdir=' + $runDirectory), ('-Dforcepush.repo.root=' + $root), '-cp', $classPath)
    if ($Test) {
        $javaArgs += @('-Djava.awt.headless=true', 'ar.edu.uade.sokoban.desktop.DesktopIntegrationTest')
        # Tests always verify the versioned demonstration file.
        $LevelPath = Join-Path $root 'src/desktop/resources/levels/demo.txt'
    } elseif ($UiTest) {
        $javaArgs += 'ar.edu.uade.sokoban.desktop.DesktopUiSmokeTest'
    } else {
        $javaArgs += 'ar.edu.uade.sokoban.desktop.SwingApplication'
    }
    if ($Extended) { $javaArgs += '--extended' }
    if ($Test -or ($explicitLevel -and -not $UiTest)) {
        & $runtime @javaArgs $LevelPath
    } else {
        & $runtime @javaArgs
    }
    if ($LASTEXITCODE -ne 0) { throw 'Swing application/tests failed.' }
    if ($Test) {
        & $runtime '-Dfile.encoding=UTF-8' '--limit-modules' 'java.base,java.desktop' '-Djava.awt.headless=true' '-cp' $classPath 'ar.edu.uade.sokoban.desktop.DesktopVisualAssetsTest' $LevelPath
        if ($LASTEXITCODE -ne 0) { throw 'UI asset tests failed.' }
        & $runtime '-Dfile.encoding=UTF-8' '--limit-modules' 'java.base,java.desktop' '-Djava.awt.headless=true' '-cp' $classPath 'ar.edu.uade.sokoban.desktop.DesktopExtensionsTest' $root
        if ($LASTEXITCODE -ne 0) { throw 'Desktop extension tests failed.' }
        & $runtime '-Dfile.encoding=UTF-8' '--limit-modules' 'java.base,java.desktop' '-Djava.awt.headless=true' '-cp' $classPath 'ar.edu.uade.sokoban.desktop.CampaignAcceptanceTest' $root
        if ($LASTEXITCODE -ne 0) { throw 'Campaign acceptance tests failed.' }
    }
    $exitCode = 0
} catch {
    Write-Error -Message $_.Exception.Message -ErrorAction Continue
} finally {
    if ($created) {
        try {
            # Only the freshly created GUID directory owned by this invocation.
            Remove-Item -LiteralPath $runDirectory -Recurse -Force
        } catch {
            Write-Error -Message ('Temporary cleanup failed: ' + $_.Exception.Message) -ErrorAction Continue
            $exitCode = 1
        }
    }
}
exit $exitCode
