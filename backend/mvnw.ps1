if (Test-Path "C:\Program Files\Java\jdk-21") {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
} elseif (Test-Path "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot") {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
}
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$MavenCmd = Join-Path $ScriptDir "..\.tools\apache-maven-3.9.6\bin\mvn.cmd"
if (Test-Path $MavenCmd) {
    & $MavenCmd $args
} else {
    mvn $args
}
