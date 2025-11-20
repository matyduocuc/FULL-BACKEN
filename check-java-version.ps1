# Script para verificar la versión de Java
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Verificación de Entorno Java" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Java instalado
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Java instalado:" -ForegroundColor Yellow
    Write-Host $javaVersion
    
    # Extraer versión
    if ($javaVersion -match '"(\d+)') {
        $majorVersion = [int]$matches[1]
        Write-Host "`nVersión mayor detectada: Java $majorVersion" -ForegroundColor $(if ($majorVersion -ge 21) { "Green" } else { "Red" })
        
        if ($majorVersion -lt 21) {
            Write-Host "`n⚠ ADVERTENCIA: Este proyecto requiere Java 21" -ForegroundColor Red
            Write-Host "Versión actual: Java $majorVersion" -ForegroundColor Red
            Write-Host "`nPara instalar Java 21:" -ForegroundColor Yellow
            Write-Host "  1. Descarga desde: https://adoptium.net/temurin/releases/?version=21" -ForegroundColor White
            Write-Host "  2. O desde: https://www.oracle.com/java/technologies/downloads/#java21" -ForegroundColor White
            Write-Host "  3. Configura JAVA_HOME apuntando a Java 21" -ForegroundColor White
            Write-Host "  4. Agrega %JAVA_HOME%\bin al PATH" -ForegroundColor White
        } else {
            Write-Host "✓ Versión de Java compatible" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "✗ Java no encontrado" -ForegroundColor Red
    Write-Host "  Instala Java 21 desde: https://adoptium.net/temurin/releases/?version=21" -ForegroundColor Yellow
}

Write-Host ""

# Verificar JAVA_HOME
if ($env:JAVA_HOME) {
    Write-Host "JAVA_HOME configurado:" -ForegroundColor Yellow
    Write-Host "  $env:JAVA_HOME" -ForegroundColor White
} else {
    Write-Host "⚠ JAVA_HOME no está configurado" -ForegroundColor Yellow
    Write-Host "  Configura la variable de entorno JAVA_HOME" -ForegroundColor White
}

Write-Host ""

# Verificar Maven
try {
    $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    Write-Host "Maven instalado:" -ForegroundColor Yellow
    Write-Host $mvnVersion
    Write-Host "✓ Maven disponible" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven no encontrado" -ForegroundColor Red
    Write-Host "`nPara instalar Maven:" -ForegroundColor Yellow
    Write-Host "  1. Descarga desde: https://maven.apache.org/download.cgi" -ForegroundColor White
    Write-Host "  2. Extrae a una carpeta (ej: C:\Program Files\Apache\maven)" -ForegroundColor White
    Write-Host "  3. Configura MAVEN_HOME apuntando a la carpeta de Maven" -ForegroundColor White
    Write-Host "  4. Agrega %MAVEN_HOME%\bin al PATH" -ForegroundColor White
    Write-Host "`nO usa el Maven Wrapper (mvnw) que viene con el proyecto" -ForegroundColor Cyan
}

Write-Host ""

