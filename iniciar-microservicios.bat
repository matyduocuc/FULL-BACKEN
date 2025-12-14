@echo off
echo ========================================
echo   INICIANDO MICROSERVICIOS BIBLIOTECA
echo ========================================
echo.
echo Abriendo 4 terminales con los microservicios...
echo.

:: Terminal 1 - User Management Service (8081)
start "Usuarios - 8081" cmd /k "cd /d C:\Users\Lenovo\Downloads\librabackend\user-management-service && echo Iniciando User Management Service (8081)... && mvnw spring-boot:run"

:: Esperar 10 segundos para que el servicio de usuarios inicie primero
echo Esperando 10 segundos para iniciar el siguiente servicio...
timeout /t 10 /nobreak

:: Terminal 2 - Book Catalog Service (8082)
start "Libros - 8082" cmd /k "cd /d C:\Users\Lenovo\Downloads\librabackend\book-catalog-service && echo Iniciando Book Catalog Service (8082)... && mvnw spring-boot:run"

:: Esperar 5 segundos
timeout /t 5 /nobreak

:: Terminal 3 - Loan Management Service (8083)
start "Prestamos - 8083" cmd /k "cd /d C:\Users\Lenovo\Downloads\librabackend\loan-management-service && echo Iniciando Loan Management Service (8083)... && mvnw spring-boot:run"

:: Esperar 5 segundos
timeout /t 5 /nobreak

:: Terminal 4 - Reports Service (8084)
start "Reportes - 8084" cmd /k "cd /d C:\Users\Lenovo\Downloads\librabackend\reports-service && echo Iniciando Reports Service (8084)... && mvnw spring-boot:run"

echo.
echo ========================================
echo   MICROSERVICIOS INICIANDO...
echo ========================================
echo.
echo Espera 1-2 minutos para que todos los servicios inicien.
echo.
echo URLs de verificacion:
echo   - Usuarios:  http://localhost:8081/swagger-ui.html
echo   - Libros:    http://localhost:8082/swagger-ui.html
echo   - Prestamos: http://localhost:8083/swagger-ui.html
echo   - Reportes:  http://localhost:8084/swagger-ui.html
echo.
echo Credenciales:
echo   Admin:   admin@biblioteca.com / admin123
echo   Usuario: usuario@biblioteca.com / user123
echo.
pause



