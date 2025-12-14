# 🚀 INSTRUCCIONES RÁPIDAS - BIBLIOTECA MICROSERVICIOS

## DSY1104 - Desarrollo Fullstack II

---

## ⚡ INICIO RÁPIDO (5 minutos)

### Paso 1: Crear Bases de Datos

Abre **MySQL Workbench** o **phpMyAdmin** y ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS library_users_db;
CREATE DATABASE IF NOT EXISTS library_books_db;
CREATE DATABASE IF NOT EXISTS library_loans_db;
```

### Paso 2: Iniciar Microservicios

Abre **4 terminales de PowerShell** y ejecuta:

**Terminal 1 - Usuarios (8081):**
```powershell
cd C:\Users\Lenovo\Downloads\librabackend\user-management-service
.\mvnw spring-boot:run
```

**Terminal 2 - Libros (8082):**
```powershell
cd C:\Users\Lenovo\Downloads\librabackend\book-catalog-service
.\mvnw spring-boot:run
```

**Terminal 3 - Préstamos (8083):**
```powershell
cd C:\Users\Lenovo\Downloads\librabackend\loan-management-service
.\mvnw spring-boot:run
```

**Terminal 4 - Reportes (8084):**
```powershell
cd C:\Users\Lenovo\Downloads\librabackend\reports-service
.\mvnw spring-boot:run
```

### Paso 3: Verificar

Abre en el navegador:
- http://localhost:8081/swagger-ui.html (Usuarios)
- http://localhost:8082/swagger-ui.html (Libros)

### Paso 4: Iniciar Frontend

```powershell
cd C:\Users\Lenovo\Downloads\library-up-main
npm run dev
```

Abre: http://localhost:5173

---

## 🔐 CREDENCIALES

| Rol | Email | Contraseña |
|-----|-------|------------|
| **Admin** | admin@biblioteca.com | admin123 |
| **Usuario** | usuario@biblioteca.com | user123 |

---

## 📚 DATOS PRECARGADOS

| Tipo | Cantidad | Descripción |
|------|----------|-------------|
| Usuarios | 2 | Admin + Usuario de prueba |
| Libros | 34 | Con stock variado |
| Sin Stock | 2 | "Lo que el viento se llevó", "El Exorcista" |

---

## 🛠️ SOLUCIÓN DE PROBLEMAS

### Error: "No se puede conectar a MySQL"

1. Verifica que MySQL esté corriendo:
   - **XAMPP**: Abre el panel y verifica que MySQL esté verde
   - **Servicio Windows**: `Get-Service MySQL*` en PowerShell

2. Verifica las bases de datos:
   ```sql
   SHOW DATABASES LIKE 'library%';
   ```

### Error: "Puerto en uso"

Cierra la aplicación que usa el puerto:
```powershell
netstat -ano | findstr :8081
taskkill /PID <numero_pid> /F
```

### Error: "JAVA_HOME no configurado"

1. Instala Java 17 o 21
2. Configura JAVA_HOME:
   ```powershell
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
   ```

---

## 📡 ENDPOINTS PRINCIPALES

### Usuarios (8081)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/auth/login | Login |
| POST | /api/auth/register | Registro |
| GET | /api/users | Listar usuarios |

### Libros (8082)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/books/all | Todos los libros |
| GET | /api/books/statistics | Estadísticas |
| PATCH | /api/books/{id}/copies | Modificar stock |

### Préstamos (8083)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/loans | Listar préstamos |
| POST | /api/loans | Crear préstamo |
| PUT | /api/loans/{id}/approve | Aprobar |
| PUT | /api/loans/{id}/return | Devolver |

---

## 📱 PANEL DE ADMIN

URL: http://localhost:5173/admin

**Secciones disponibles:**
- 📊 Dashboard - Estadísticas generales
- 📚 Libros - CRUD de libros
- 👥 Usuarios - Gestión de usuarios
- 📋 Préstamos - Aprobar/rechazar préstamos
- 📦 Stock - Panel de inventario (NUEVO)

---

## ✅ CHECKLIST DE VERIFICACIÓN

- [ ] MySQL corriendo
- [ ] 3 bases de datos creadas
- [ ] user-management-service corriendo (8081)
- [ ] book-catalog-service corriendo (8082)
- [ ] loan-management-service corriendo (8083)
- [ ] reports-service corriendo (8084)
- [ ] Frontend corriendo (5173)
- [ ] Login con admin@biblioteca.com funciona
- [ ] Catálogo muestra 34 libros
- [ ] Panel de admin accesible

---

**Última actualización:** Diciembre 2024



