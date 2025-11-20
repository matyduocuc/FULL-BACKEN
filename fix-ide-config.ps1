# Script para configurar el IDE automáticamente
Write-Host "Configurando proyectos para IDE..." -ForegroundColor Cyan

$services = @(
    "user-management-service",
    "book-catalog-service", 
    "loan-management-service",
    "reports-service",
    "notifications-service"
)

foreach ($service in $services) {
    Write-Host "`nConfigurando $service..." -ForegroundColor Yellow
    
    # Crear carpeta .settings si no existe
    $settingsPath = "$service\.settings"
    if (-not (Test-Path $settingsPath)) {
        New-Item -ItemType Directory -Path $settingsPath -Force | Out-Null
    }
    
    # Configurar Java 21
    $jdtContent = @"
eclipse.preferences.version=1
org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode=enabled
org.eclipse.jdt.core.compiler.codegen.targetPlatform=21
org.eclipse.jdt.core.compiler.compliance=21
org.eclipse.jdt.core.compiler.problem.assertIdentifier=error
org.eclipse.jdt.core.compiler.problem.enablePreviewFeatures=disabled
org.eclipse.jdt.core.compiler.problem.enumIdentifier=error
org.eclipse.jdt.core.compiler.problem.forbiddenReference=warning
org.eclipse.jdt.core.compiler.problem.reportPreviewFeatures=ignore
org.eclipse.jdt.core.compiler.release=disabled
org.eclipse.jdt.core.compiler.source=21
"@
    Set-Content -Path "$settingsPath\org.eclipse.jdt.core.prefs" -Value $jdtContent
    
    # Configurar Maven
    $m2eContent = @"
activeProfiles=
eclipse.preferences.version=1
resolveWorkspaceProjects=true
version=1
"@
    Set-Content -Path "$settingsPath\org.eclipse.m2e.core.prefs" -Value $m2eContent
    
    # Configurar encoding
    $resourcesContent = @"
eclipse.preferences.version=1
encoding/<project>=UTF-8
"@
    Set-Content -Path "$settingsPath\org.eclipse.core.resources.prefs" -Value $resourcesContent
    
    Write-Host "✓ $service configurado" -ForegroundColor Green
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Configuración completada!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nProximos pasos:" -ForegroundColor Yellow
Write-Host "1. Abre el IDE (Eclipse/Spring Tool Suite)" -ForegroundColor White
Write-Host "2. File -> Import -> Existing Maven Projects" -ForegroundColor White
Write-Host "3. Selecciona la carpeta raiz del proyecto" -ForegroundColor White
Write-Host "4. Asegurate de tener Java 21 configurado" -ForegroundColor White
Write-Host "5. Click derecho en cada proyecto -> Maven -> Update Project" -ForegroundColor White

