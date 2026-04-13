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

$surefireDir = Join-Path $resolvedProjectRoot "backend\target\surefire-reports"
$reportsDir = Join-Path $PSScriptRoot "reports"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$bundleDir = Join-Path $reportsDir "surefire-$timestamp"

if (-not (Test-Path $surefireDir)) {
    throw "No surefire reports found. Run tests first."
}

New-Item -ItemType Directory -Force -Path $bundleDir | Out-Null
Copy-Item -Path (Join-Path $surefireDir "*") -Destination $bundleDir -Recurse -Force

Write-Host "[Sprint3-State1] Surefire reports exported to: $bundleDir" -ForegroundColor Green
