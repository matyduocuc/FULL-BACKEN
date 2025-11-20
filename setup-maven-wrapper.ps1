# Script para crear Maven Wrapper en todos los proyectos
Write-Host "Configurando Maven Wrapper..." -ForegroundColor Cyan

$services = @(
    "user-management-service",
    "book-catalog-service",
    "loan-management-service",
    "reports-service",
    "notifications-service"
)

$mvnwUrl = "https://raw.githubusercontent.com/takari/maven-wrapper/master/maven-wrapper.jar"
$mvnwProps = @"
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.5/apache-maven-3.9.5-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
"@

foreach ($service in $services) {
    Write-Host "`nConfigurando $service..." -ForegroundColor Yellow
    
    if (Test-Path $service) {
        $mvnDir = Join-Path $service ".mvn"
        $wrapperDir = Join-Path $mvnDir "wrapper"
        
        # Crear directorios
        if (-not (Test-Path $mvnDir)) {
            New-Item -ItemType Directory -Path $mvnDir -Force | Out-Null
        }
        if (-not (Test-Path $wrapperDir)) {
            New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
        }
        
        # Crear maven-wrapper.properties
        $propsPath = Join-Path $wrapperDir "maven-wrapper.properties"
        $mvnwProps | Out-File -FilePath $propsPath -Encoding UTF8
        
        Write-Host "  ✓ Maven Wrapper configurado para $service" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Carpeta $service no encontrada" -ForegroundColor Red
    }
}

Write-Host "`n✓ Configuración completada!" -ForegroundColor Green
Write-Host "`nNota: Necesitarás descargar maven-wrapper.jar manualmente o instalar Maven" -ForegroundColor Yellow

