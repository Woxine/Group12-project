[CmdletBinding()]
param(
    [string]$ProjectRoot = ""
)

$ErrorActionPreference = "Stop"

$resolvedProjectRoot = if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
} else {
    (Resolve-Path $ProjectRoot).Path
}

$backendPom = Join-Path $resolvedProjectRoot "backend\pom.xml"
$reportsDir = Join-Path $PSScriptRoot "reports"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$logFile = Join-Path $reportsDir "sprint2-all-$timestamp.log"

if (-not (Test-Path $backendPom)) {
    throw "Cannot find backend pom.xml at: $backendPom"
}

New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null

$mavenCmd = (Get-Command mvn -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Source)
if (-not $mavenCmd) {
    $mavenWrapper = Join-Path $resolvedProjectRoot "backend\mvnw.cmd"
    if (Test-Path $mavenWrapper) {
        $mavenCmd = $mavenWrapper
    } else {
        throw "Neither mvn nor backend\\mvnw.cmd is available."
    }
}

Write-Host "[Sprint2] Running all unit tests..."
Write-Host "[Sprint2] Log file: $logFile"
Write-Host "[Sprint2] Maven command: $mavenCmd"

$args = @(
    "-f", $backendPom,
    "-DtrimStackTrace=false",
    "-Dsurefire.printSummary=true",
    "-DforkCount=1",
    "-DreuseForks=false",
    "-DfailIfNoTests=false",
    "-DargLine=-Dnet.bytebuddy.experimental=true",
    "test"
)

$prevEap = $ErrorActionPreference
$ErrorActionPreference = "Continue"
try {
    & $mavenCmd @args 2>&1 | Tee-Object -FilePath $logFile
    $exitCode = $LASTEXITCODE
} finally {
    $ErrorActionPreference = $prevEap
}

if ($exitCode -ne 0) {
    Write-Host "[Sprint2] FAILED (exit code: $exitCode)" -ForegroundColor Red
    exit $exitCode
}

Write-Host "[Sprint2] PASSED" -ForegroundColor Green
Write-Host "[Sprint2] Full output saved to: $logFile"
