$ErrorActionPreference = "Stop"

$failures = 0

function Pass-Check {
    param([string] $Message)
    Write-Host "PASS: $Message"
}

function Fail-Check {
    param([string] $Message)
    Write-Host "FAIL: $Message"
    $script:failures += 1
}

function Test-FilePattern {
    param(
        [string] $Path,
        [string] $Pattern
    )

    $content = Get-Content -LiteralPath $Path -Raw
    return $content -match $Pattern
}

function Test-NginxForwardsAuthorization {
    $file = "frontend/nginx/default.conf"
    $pattern = 'proxy_set_header\s+Authorization\s+\$http_authorization;'

    if (Test-FilePattern -Path $file -Pattern $pattern) {
        Pass-Check "nginx forwards Authorization headers to the backend API"
    } else {
        Fail-Check "nginx must forward Authorization headers in the /api proxy block"
    }
}

function Test-MockAuthDisabled {
    $file = "frontend/src/api/api.js"
    $pattern = 'const\s+USE_MOCK_AUTH\s*=\s*false;'

    if (Test-FilePattern -Path $file -Pattern $pattern) {
        Pass-Check "frontend mock authentication is disabled"
    } else {
        Fail-Check "frontend mock authentication must stay disabled for CI and production builds"
    }
}

function Test-FrontendBackendApiContract {
    $apiFile = "frontend/src/api/api.js"
    $apiContent = Get-Content -LiteralPath $apiFile -Raw
    $controllerContent = (
        Get-Content -LiteralPath "backend/gde_website/src/main/java/gde/gde_website/users/controller/UsersController.java" -Raw
    ) + "`n" + (
        Get-Content -LiteralPath "backend/gde_website/src/main/java/gde/gde_website/games/controller/GamesController.java" -Raw
    )

    $frontendPatterns = @(
        'request\("/auth/register"',
        'request\("/auth/login"',
        'request\("/auth/me"',
        'request\(`/games\?page=',
        'request\(`/games/\$\{id\}`',
        'request\("/games"',
        'request\(`/games/author/\$\{authorId\}`',
        'request\("/games/tags/all"'
    )

    $backendPatterns = @(
        '@PostMapping\("/register"\)',
        '@PostMapping\("/login"\)',
        '@GetMapping\("/me"\)',
        '@GetMapping',
        '@GetMapping\("/\{id\}"\)',
        '@PostMapping',
        '@PatchMapping\(path = "/\{id\}"',
        '@DeleteMapping\("/\{id\}"\)',
        '@GetMapping\("/author/\{id\}"\)',
        '@GetMapping\("/tags/all"\)'
    )

    $missing = $false

    foreach ($pattern in $frontendPatterns) {
        if ($apiContent -notmatch $pattern) {
            Write-Host "Missing frontend API contract pattern: $pattern"
            $missing = $true
        }
    }

    foreach ($pattern in $backendPatterns) {
        if ($controllerContent -notmatch $pattern) {
            Write-Host "Missing backend API contract pattern: $pattern"
            $missing = $true
        }
    }

    if ($missing) {
        Fail-Check "frontend/backend API route contract drift detected"
    } else {
        Pass-Check "frontend API client and backend controllers expose the expected core routes"
    }
}

Test-NginxForwardsAuthorization
Test-MockAuthDisabled
Test-FrontendBackendApiContract

if ($failures -gt 0) {
    Write-Host ""
    Write-Host "$failures additional QA check(s) failed."
    exit 1
}

Write-Host ""
Write-Host "All additional QA checks passed."
