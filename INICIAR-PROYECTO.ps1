# ============================================
# SCRIPT DE INICIO - BIBLIOTECA MICROSERVICIOS
# DSY1104 - Desarrollo Fullstack II
# ============================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  BIBLIOTECA - SISTEMA DE MICROSERVICIOS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si MySQL esta corriendo
Write-Host "[1/5] Verificando MySQL..." -ForegroundColor Yellow
$mysql = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue
if ($mysql) {
    if ($mysql.Status -eq "Running") {
        Write-Host "  OK MySQL esta corriendo" -ForegroundColor Green
    } else {
        Write-Host "  MySQL encontrado pero no esta corriendo. Iniciando..." -ForegroundColor Yellow
        Start-Service -Name $mysql.Name
        Write-Host "  OK MySQL iniciado" -ForegroundColor Green
    }
} else {
    Write-Host "  AVISO: Servicio MySQL no encontrado como servicio de Windows" -ForegroundColor Yellow
    Write-Host "  Si usas XAMPP, asegurate de que MySQL este iniciado en el panel de XAMPP" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[2/5] Creando bases de datos..." -ForegroundColor Yellow
Write-Host "  Ejecuta este SQL en MySQL Workbench o phpMyAdmin:" -ForegroundColor White
Write-Host ""
Write-Host "  CREATE DATABASE IF NOT EXISTS library_users_db;" -ForegroundColor Gray
Write-Host "  CREATE DATABASE IF NOT EXISTS library_books_db;" -ForegroundColor Gray
Write-Host "  CREATE DATABASE IF NOT EXISTS library_loans_db;" -ForegroundColor Gray
Write-Host ""

Write-Host "[3/5] Instrucciones para iniciar microservicios:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  ABRE 4 TERMINALES SEPARADAS y ejecuta:" -ForegroundColor White
Write-Host ""
Write-Host "  Terminal 1 (Usuarios - Puerto 8081):" -ForegroundColor Cyan
Write-Host "    cd C:\Users\Lenovo\Downloads\librabackend\user-management-service" -ForegroundColor Gray
Write-Host "    .\mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  Terminal 2 (Libros - Puerto 8082):" -ForegroundColor Cyan
Write-Host "    cd C:\Users\Lenovo\Downloads\librabackend\book-catalog-service" -ForegroundColor Gray
Write-Host "    .\mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  Terminal 3 (Prestamos - Puerto 8083):" -ForegroundColor Cyan
Write-Host "    cd C:\Users\Lenovo\Downloads\librabackend\loan-management-service" -ForegroundColor Gray
Write-Host "    .\mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  Terminal 4 (Reportes - Puerto 8084):" -ForegroundColor Cyan
Write-Host "    cd C:\Users\Lenovo\Downloads\librabackend\reports-service" -ForegroundColor Gray
Write-Host "    .\mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "[4/5] URLs para verificar:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  Swagger UI:" -ForegroundColor White
Write-Host "    - Usuarios:   http://localhost:8081/swagger-ui.html" -ForegroundColor Gray
Write-Host "    - Libros:     http://localhost:8082/swagger-ui.html" -ForegroundColor Gray
Write-Host "    - Prestamos:  http://localhost:8083/swagger-ui.html" -ForegroundColor Gray
Write-Host "    - Reportes:   http://localhost:8084/swagger-ui.html" -ForegroundColor Gray
Write-Host ""
Write-Host "  Health Check:" -ForegroundColor White
Write-Host "    - http://localhost:8081/actuator/health" -ForegroundColor Gray
Write-Host "    - http://localhost:8082/actuator/health" -ForegroundColor Gray
Write-Host ""

Write-Host "[5/5] Credenciales de acceso:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  ADMINISTRADOR:" -ForegroundColor Green
Write-Host "    Email:    admin@biblioteca.com" -ForegroundColor White
Write-Host "    Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "  USUARIO:" -ForegroundColor Blue
Write-Host "    Email:    usuario@biblioteca.com" -ForegroundColor White
Write-Host "    Password: user123" -ForegroundColor White
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DATOS PRECARGADOS AUTOMATICAMENTE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  - 2 usuarios (admin + usuario de prueba)" -ForegroundColor White
Write-Host "  - 34 libros con stock variado" -ForegroundColor White
Write-Host "  - 2 libros SIN stock (para demostrar alertas)" -ForegroundColor White
Write-Host ""
Write-Host "  Los datos se cargan automaticamente cuando:" -ForegroundColor Yellow
Write-Host "  1. MySQL esta corriendo" -ForegroundColor Gray
Write-Host "  2. Las bases de datos existen" -ForegroundColor Gray
Write-Host "  3. Los microservicios inician por primera vez" -ForegroundColor Gray
Write-Host ""

Write-Host "Presiona cualquier tecla para continuar..." -ForegroundColor DarkGray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")



