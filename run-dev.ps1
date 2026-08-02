# Loads .env into the environment, then launches one service (in this
# window) or all services at once (each in its own new window) via the
# Maven wrapper. Having one script for both cases avoids re-loading
# .env by hand every time and avoids forgetting to start a dependency
# (e.g. testing Transaction service without Account service running).
#
# Usage:
#   ./run-dev.ps1 helix-account-service        # run one service here
#   ./run-dev.ps1 all                           # run every service, each in its own window
#   ./run-dev.ps1 infra                         # docker-compose up -d (mysql, kafka, rabbitmq)

param(
    [Parameter(Mandatory = $true)]
    [string]$Target
)

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Error ".env not found at $envFile. Copy .env.example to .env first."
    exit 1
}

function Load-DotEnv {
    Write-Host "Loading environment variables from .env ..." -ForegroundColor Cyan
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "  Loaded: $name"
        }
    }
}

function Start-OneService {
    param([string]$ServiceModule)

    $servicePath = Join-Path $PSScriptRoot $ServiceModule
    if (-not (Test-Path $servicePath)) {
        Write-Error "Service folder not found: $servicePath"
        return
    }

    Write-Host "`nStarting $ServiceModule ...`n" -ForegroundColor Cyan
    Push-Location $servicePath
    try {
        & "$PSScriptRoot\mvnw.cmd" spring-boot:run
    }
    finally {
        Pop-Location
    }
}

# All known runnable service modules, in the order they should start
# (Account first, since Transaction/Card/Fraud depend on it via Feign).
$allServices = @(
    "helix-account-service",
    "helix-transaction-service",
    "helix-card-service",
    "helix-fraud-service",
    "helix-gateway"
)

switch ($Target) {

    "infra" {
        Write-Host "Starting infrastructure (MySQL, Kafka, RabbitMQ) ..." -ForegroundColor Cyan
        docker-compose --env-file $envFile up -d mysql kafka rabbitmq
        Write-Host "`nRun 'docker ps' to confirm all three show (healthy) before starting services." -ForegroundColor Yellow
    }

    "all" {
        Load-DotEnv

        foreach ($service in $allServices) {
            $servicePath = Join-Path $PSScriptRoot $service
            if (-not (Test-Path $servicePath)) {
                Write-Host "Skipping $service (folder not found yet - not built in this phase)" -ForegroundColor DarkYellow
                continue
            }

            Write-Host "Launching $service in a new window ..." -ForegroundColor Cyan

            # Each new window re-loads .env itself (via -Command below) since
            # environment variables set with SetEnvironmentVariable("Process")
            # only apply to THIS process, not to newly spawned windows.
            $cmd = "cd `"$PSScriptRoot`"; ./run-dev.ps1 $service"
            Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd

            # Small delay so services start in roughly the intended order
            # and logs from each window are readable as they spin up.
            Start-Sleep -Seconds 3
        }

        Write-Host "`nAll available services launching in separate windows. Check each window for 'Tomcat started on port ...'." -ForegroundColor Green
    }

    default {
        Load-DotEnv
        Start-OneService -ServiceModule $Target
    }
}