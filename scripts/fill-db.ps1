param(
    [string]$ScriptsDir = (Join-Path $PSScriptRoot "db_scripts\mock_scripts"),
    [string]$EnvFile = (Join-Path (Split-Path $PSScriptRoot -Parent) ".env.secret"),
    [string]$Service = "postgres",
    [string]$Database,
    [string]$DbUser
)

$ErrorActionPreference = "Stop"

function Get-DotEnvValue {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    $line = Get-Content -LiteralPath $Path |
        Where-Object { $_ -match "^\s*$([regex]::Escape($Name))\s*=" } |
        Select-Object -First 1

    if (-not $line) {
        return $null
    }

    return ($line -replace "^\s*$([regex]::Escape($Name))\s*=\s*", "").Trim('"').Trim("'")
}

if (-not $Database) {
    $Database = Get-DotEnvValue -Path $EnvFile -Name "POSTGRES_DB"
}

if (-not $DbUser) {
    $DbUser = Get-DotEnvValue -Path $EnvFile -Name "POSTGRES_USER"
}

if (-not $Database) {
    throw "Database name was not provided and POSTGRES_DB was not found in $EnvFile."
}

if (-not $DbUser) {
    throw "Database user was not provided and POSTGRES_USER was not found in $EnvFile."
}

if (-not (Test-Path -LiteralPath $ScriptsDir)) {
    throw "Scripts directory was not found: $ScriptsDir"
}

$scripts = Get-ChildItem -LiteralPath $ScriptsDir -Filter "*.sql" -File | Sort-Object Name

if (-not $scripts) {
    throw "No .sql files found in $ScriptsDir."
}

Write-Host "Running $($scripts.Count) mock database script(s) against database '$Database' as user '$DbUser'."

foreach ($script in $scripts) {
    Write-Host "Running $($script.Name)..."

    Get-Content -LiteralPath $script.FullName -Raw |
        docker compose exec -T $Service psql -v ON_ERROR_STOP=1 -U $DbUser -d $Database

    if ($LASTEXITCODE -ne 0) {
        throw "Failed while running $($script.FullName)."
    }
}

Write-Host "Mock database scripts completed successfully."
