<#
.SYNOPSIS
  Script to start the local development environment

.DESCRIPTION
  1. Sets environment variables for the local database
  2. Runs adb reverse for port 8080 so the phone can access the local server
  3. Starts the backend Spring Boot project
#>

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "    Start Group12 Scooter Local Env       " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 1. Set environment variables
Write-Host "`n[1/3] Setting SQL/Environment to Local ..." -ForegroundColor Yellow
$env:SPRING_PROFILES_ACTIVE = "local"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/scooter_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
Write-Host "Environment variables set: SPRING_PROFILES_ACTIVE=local" -ForegroundColor Green

# 2. Port reverse
Write-Host "`n[2/3] Reversing port (adb reverse tcp:8080 tcp:8080) ..." -ForegroundColor Yellow
try {
    adb reverse tcp:8080 tcp:8080
    Write-Host "Port reverse command executed successfully." -ForegroundColor Green
} catch {
    Write-Host "Warning: Port reverse failed. Please ensure your device is connected with USB debugging enabled." -ForegroundColor Red
}

# 3. Start backend
Write-Host "`n[3/3] Starting backend Spring Boot ..." -ForegroundColor Yellow
$BackendPath = Join-Path -Path $PSScriptRoot -ChildPath "backend"

if (Test-Path $BackendPath) {
    Set-Location -Path $BackendPath
    Write-Host "Running ./mvnw.cmd spring-boot:run ..." -ForegroundColor Cyan
    
    if (Test-Path ".\mvnw.cmd") {
        .\mvnw.cmd spring-boot:run
    } else {
        mvn spring-boot:run
    }
} else {
    Write-Host "Error: Cannot find 'backend' folder." -ForegroundColor Red
}
