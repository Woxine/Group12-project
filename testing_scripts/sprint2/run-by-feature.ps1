[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("id2", "id3", "id7", "id10_11")]
    [string]$Feature,
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
$configPath = Join-Path $PSScriptRoot "config\features.psd1"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$logFile = Join-Path $reportsDir "sprint2-$Feature-$timestamp.log"

if (-not (Test-Path $backendPom)) {
    throw "Cannot find backend pom.xml at: $backendPom"
}
if (-not (Test-Path $configPath)) {
    throw "Cannot find feature config at: $configPath"
}

$config = Import-PowerShellDataFile -Path $configPath
$patterns = $config.Features[$Feature]
if (-not $patterns -or $patterns.Count -eq 0) {
    throw "No test patterns configured for feature: $Feature"
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

$testSelector = ($patterns -join ",")

Write-Host "[Sprint2][$Feature] Running targeted unit tests..."
Write-Host "[Sprint2][$Feature] Test selector: $testSelector"
Write-Host "[Sprint2][$Feature] Log file: $logFile"
Write-Host "[Sprint2][$Feature] Maven command: $mavenCmd"

$args = @(
    "-f", $backendPom,
    "-DtrimStackTrace=false",
    "-Dsurefire.printSummary=true",
    "-DforkCount=1",
    "-DreuseForks=false",
    "-DfailIfNoTests=false",
    "-Dsurefire.failIfNoSpecifiedTests=false",
    "-Dtest=$testSelector",
    "test"
)

# ErrorAction Stop + 2>&1 会把 JVM 打到 stderr 的 WARNING 当成终止错误，中断 Maven。
$prevEap = $ErrorActionPreference
$ErrorActionPreference = "Continue"
try {
    & $mavenCmd @args 2>&1 | Tee-Object -FilePath $logFile
    $exitCode = $LASTEXITCODE
} finally {
    $ErrorActionPreference = $prevEap
}

if ($exitCode -ne 0) {
    Write-Host "[Sprint2][$Feature] FAILED (exit code: $exitCode)" -ForegroundColor Red
    exit $exitCode
}

Write-Host "[Sprint2][$Feature] PASSED" -ForegroundColor Green
Write-Host "[Sprint2][$Feature] Full output saved to: $logFile"
