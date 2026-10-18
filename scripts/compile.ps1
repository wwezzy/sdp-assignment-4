param(
    [switch]$IncludeTests,
    [string]$JdkHome = $env:JAVA_HOME
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

if (-not $JdkHome) {
    $compilerCommand = Get-Command javac -ErrorAction SilentlyContinue
    if ($compilerCommand) {
        $JdkHome = Split-Path -Parent (Split-Path -Parent $compilerCommand.Source)
    } else {
        $jdkDirectory = Join-Path $env:USERPROFILE '.jdks'
        if (Test-Path -LiteralPath $jdkDirectory) {
            $installedJdk = Get-ChildItem -LiteralPath $jdkDirectory -Directory |
                Where-Object {
                    $_.Name -match '(^|[-_])17([.\-_]|$)' -and
                    (Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe'))
                } |
                Sort-Object Name -Descending |
                Select-Object -First 1
            if ($installedJdk) {
                $JdkHome = $installedJdk.FullName
            }
        }
    }
}

if (-not $JdkHome) {
    throw 'JDK not found. Set JAVA_HOME or pass -JdkHome to this script.'
}

$compilerPath = Join-Path $JdkHome 'bin\javac.exe'
$javaPath = Join-Path $JdkHome 'bin\java.exe'
if (-not (Test-Path -LiteralPath $compilerPath) -or -not (Test-Path -LiteralPath $javaPath)) {
    throw 'The selected JDK must contain bin\javac.exe and bin\java.exe.'
}

$outputDirectory = Join-Path $projectRoot 'out\main'
$sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })

if ($IncludeTests) {
    $outputDirectory = Join-Path $projectRoot 'out\test'
    $sourceFiles += @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'tests') -Recurse -Filter '*.java' |
        ForEach-Object { $_.FullName })
}

New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
& $compilerPath --release 17 -Xlint:all -d $outputDirectory $sourceFiles
if ($LASTEXITCODE -ne 0) {
    throw 'Java compilation failed.'
}

[PSCustomObject]@{
    Java = $javaPath
    Output = $outputDirectory
}

