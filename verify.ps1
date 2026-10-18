param([string]$JdkHome = $env:JAVA_HOME)

$ErrorActionPreference = 'Stop'
$build = & (Join-Path $PSScriptRoot 'scripts\compile.ps1') -IncludeTests -JdkHome $JdkHome
& $build.Java -cp $build.Output kz.aitu.sdp.facade.HomeTheaterTests
if ($LASTEXITCODE -ne 0) {
    throw 'The verification failed.'
}

