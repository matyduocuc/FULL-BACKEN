# 🏗️ MAPA ARQUITECTURA BACKEND - Sistema de Biblioteca

## 📋 Índice
1. [Vista General del Sistema](#-vista-general-del-sistema)
2. [Los 5 Microservicios](#-los-5-microservicios)
3. [Flujo de Datos entre Servicios](#-flujo-de-datos-entre-servicios)
4. [Detalle de cada Microservicio](#-detalle-de-cada-microservicio)
5. [Tablas de Base de Datos](#-tablas-de-base-de-datos)
6. [Todos los Endpoints](#-todos-los-endpoints)

---

## 🌐 Vista General del Sistema

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                        🌐 CLIENTES                                                   │
│                      (Frontend Web / Android App / Postman / Swagger UI)                            │
└─────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                │
                                          HTTP/REST
                                                │
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    📡 API GATEWAY (Implícito)                                        │
│                                     Cada servicio expone su API                                      │
└─────────────────────────────────────────────────────────────────────────────────────────────────────┘
           │                    │                    │                    │                    │
           ▼                    ▼                    ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ 👤 USER         │  │ 📚 BOOK         │  │ 📖 LOAN         │  │ 📊 REPORTS      │  │ 🔔 NOTIFICATIONS│
│ MANAGEMENT      │  │ CATALOG         │  │ MANAGEMENT      │  │ SERVICE         │  │ SERVICE         │
│ SERVICE         │  │ SERVICE         │  │ SERVICE         │  │                 │  │                 │
│                 │  │                 │  │                 │  │                 │  │                 │
│ Puerto: 8081    │  │ Puerto: 8082    │  │ Puerto: 8083    │  │ Puerto: 8084    │  │ Puerto: 8085    │
│                 │  │                 │  │                 │  │                 │  │                 │
│ • Registro      │  │ • CRUD libros   │  │ • Crear préstamo│  │ • Dashboard     │  │ • Crear notif.  │
│ • Login/Logout  │  │ • Búsqueda      │  │ • Devolución    │  │ • Estadísticas  │  │ • Listar        │
│ • JWT tokens    │  │ • Categorías    │  │ • Extensión     │  │                 │  │ • Marcar leída  │
│ • Roles         │  │ • Disponibilidad│  │ • Multas        │  │                 │  │                 │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │                    │                    │
         ▼                    ▼                    ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ 🗄️ MySQL        │  │ 🗄️ MySQL        │  │ 🗄️ MySQL        │  │ 🗄️ MySQL        │  │ 🗄️ MySQL        │
│                 │  │                 │  │                 │  │                 │  │                 │
│ library_users_db│  │ library_books_db│  │ library_loans_db│  │ library_        │  │ library_        │
│                 │  │                 │  │                 │  │ reports_db      │  │ notifications_db│
│ Tablas:         │  │ Tablas:         │  │ Tablas:         │  │ Tablas:         │  │ Tablas:         │
│ • users         │  │ • books         │  │ • loans         │  │ • reports       │  │ • notifications │
│ • sessions      │  │                 │  │ • loan_history  │  │                 │  │                 │
│ • auditoria     │  │                 │  │                 │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
```

---

## 🔗 Comunicación entre Microservicios

```
                                    ┌───────────────────────┐
                                    │  📖 LOAN MANAGEMENT   │
                                    │      (Puerto 8083)    │
                                    └───────────┬───────────┘
                                                │
                    ┌───────────────────────────┼───────────────────────────┐
                    │                           │                           │
                    ▼                           ▼                           ▼
       ┌────────────────────┐      ┌────────────────────┐      ┌────────────────────┐
       │  👤 USER SERVICE   │      │  📚 BOOK SERVICE   │      │  🔔 NOTIFICATION   │
       │    (Puerto 8081)   │      │    (Puerto 8082)   │      │    (Puerto 8085)   │
       │                    │      │                    │      │                    │
       │ • Valida usuario   │      │ • Verifica         │      │ • Envía alertas    │
       │ • Verifica token   │      │   disponibilidad   │      │   de vencimiento   │
       │ • Obtiene datos    │      │ • Actualiza copias │      │ • Notifica         │
       └────────────────────┘      └────────────────────┘      │   préstamos        │
                                                               └────────────────────┘

       ┌────────────────────────────────────────────────────────────────────────────┐
       │                        📊 REPORTS SERVICE (Puerto 8084)                     │
       │                                                                             │
       │  Consume datos de:                                                          │
       │  • User Service → Total usuarios, usuarios activos                          │
       │  • Book Service → Total libros, disponibles                                 │
       │  • Loan Service → Préstamos activos, vencidos                               │
       └────────────────────────────────────────────────────────────────────────────┘
```

---

## 🏛️ Arquitectura de Capas (Cada Microservicio)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              ESTRUCTURA POR CAPAS                                │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│   📁 config/           ⚙️ CONFIGURACIÓN                                          │
│   ├── SecurityConfig.java      → Seguridad, rutas públicas/privadas             │
│   ├── JwtConfig.java           → Configuración JWT                               │
│   └── DataInitializer.java     → Datos iniciales al arrancar                    │
│                                                                                  │
│   📁 controller/       🎮 CAPA DE PRESENTACIÓN (HTTP)                            │
│   └── XxxController.java       → Recibe HTTP, retorna JSON                      │
│       • @RestController        → "Soy un controlador REST"                      │
│       • @RequestMapping        → "Mi ruta base es /api/xxx"                     │
│       • @GetMapping, @PostMapping, etc.                                         │
│                                                                                  │
│   📁 dto/              📦 OBJETOS DE TRANSFERENCIA                               │
│   ├── XxxCreateDTO.java        → Datos de entrada para crear                    │
│   ├── XxxUpdateDTO.java        → Datos de entrada para actualizar               │
│   └── XxxResponseDTO.java      → Datos de salida (nunca password)               │
│                                                                                  │
│   📁 service/          ⚙️ CAPA DE NEGOCIO (LÓGICA)                               │
│   └── XxxService.java          → Lógica de negocio                              │
│       • @Service               → "Soy un servicio"                              │
│       • @Transactional         → "Mis operaciones son atómicas"                 │
│       • Valida, transforma, procesa                                             │
│                                                                                  │
│   📁 repository/       📚 CAPA DE DATOS (ACCESO A BD)                            │
│   └── XxxRepository.java       → Interface JPA                                  │
│       • extends JpaRepository  → CRUD automático                                │
│       • Query methods          → findByXxx, existsByXxx                         │
│                                                                                  │
│   📁 model/            🏛️ ENTIDADES (TABLAS)                                     │
│   └── Xxx.java                 → Mapea a tabla de BD                            │
│       • @Entity                → "Soy una entidad JPA"                          │
│       • @Table(name="xxx")     → "Mi tabla se llama xxx"                        │
│       • @Id, @Column           → Mapeo de columnas                              │
│                                                                                  │
│   📁 security/         🔒 SEGURIDAD                                              │
│   ├── JwtAuthenticationFilter  → Intercepta y valida tokens                     │
│   └── JwtUtil.java             → Genera/valida JWT                              │
│                                                                                  │
│   📁 exception/        ⚠️ MANEJO DE ERRORES                                      │
│   └── GlobalExceptionHandler   → Captura excepciones, retorna JSON error        │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Detalle de cada Microservicio

### 1️⃣ USER MANAGEMENT SERVICE (Puerto 8081)

**Responsabilidad**: Gestión de usuarios y autenticación JWT

```
📁 user-management-service/
│
├── 📁 model/
│   ├── User.java           → Tabla: users
│   │   ├── id (Long)
│   │   ├── name (String)
│   │   ├── email (String) [unique]
│   │   ├── password (String) [BCrypt hash]
│   │   ├── role (USUARIO | ADMINISTRADOR)
│   │   ├── status (ACTIVO | BLOQUEADO)
│   │   ├── createdAt, updatedAt
│   │
│   ├── Session.java        → Tabla: sessions
│   │   ├── id, userId, token, expiresAt
│   │
│   └── Auditoria.java      → Tabla: auditoria
│
├── 📁 dto/
│   ├── UserRegistrationDTO → Entrada: {name, email, password, phone}
│   ├── UserLoginDTO        → Entrada: {email, password}
│   ├── UserResponseDTO     → Salida: {id, name, email, role, status...}
│   ├── LoginResponseDTO    → Salida: {token, user, expiresIn}
│   └── TokenValidationDTO  → Para validar JWT
│
├── 📁 service/
│   └── UserService.java
│       ├── register()      → Crea usuario + encripta password
│       ├── login()         → Valida + genera JWT + crea sesión
│       ├── logout()        → Invalida sesión
│       ├── updateUser()    → Actualiza datos
│       ├── blockUser()     → Bloquea/desbloquea
│       └── changeRole()    → Cambia USUARIO ↔ ADMINISTRADOR
│
└── 📁 repository/
    └── UserRepository.java
        ├── findByEmail()
        ├── existsByEmail()
        └── countByStatus()
```

**Endpoints**:
| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/users/register` | Registrar usuario | ❌ |
| POST | `/api/users/login` | Iniciar sesión | ❌ |
| POST | `/api/users/logout` | Cerrar sesión | ✅ |
| GET | `/api/users/{id}` | Obtener usuario | ✅ |
| PUT | `/api/users/{id}` | Actualizar usuario | ✅ |
| DELETE | `/api/users/{id}` | Eliminar usuario | ✅ |
| PATCH | `/api/users/{id}/block` | Bloquear/desbloquear | ✅👑 |
| PATCH | `/api/users/{id}/role` | Cambiar rol | ✅👑 |
| GET | `/api/users` | Listar todos | ✅ |

---

### 2️⃣ BOOK CATALOG SERVICE (Puerto 8082)

**Responsabilidad**: Catálogo de libros, búsqueda, disponibilidad

```
📁 book-catalog-service/
│
├── 📁 model/
│   └── Book.java           → Tabla: books
│       ├── id (Long)
│       ├── title (String)
│       ├── author (String)
│       ├── isbn (String) [unique]
│       ├── category (String)
│       ├── publisher (String)
│       ├── year (Integer)
│       ├── description (String)
│       ├── coverUrl (String)
│       ├── status (AVAILABLE | LOANED | RESERVED)
│       ├── totalCopies (Integer)
│       ├── availableCopies (Integer)
│       ├── price (BigDecimal)
│       ├── featured (Boolean)
│       └── createdAt, updatedAt
│
├── 📁 dto/
│   ├── BookCreateDTO       → Entrada: {title, author, isbn...}
│   ├── BookUpdateDTO       → Entrada parcial para actualizar
│   ├── BookResponseDTO     → Salida completa del libro
│   ├── BookAvailabilityDTO → {bookId, available, totalCopies, availableCopies}
│   └── BookStatisticsDTO   → Estadísticas del catálogo
│
├── 📁 service/
│   ├── BookService.java
│   │   ├── createBook()
│   │   ├── updateBook()
│   │   ├── deleteBook()
│   │   ├── searchBooks()       → Búsqueda por título/autor/ISBN
│   │   ├── checkAvailability() → ¿Hay copias disponibles?
│   │   ├── updateCopies()      → +/- copias al prestar/devolver
│   │   └── getBookStatistics()
│   │
│   └── BookSeedService.java    → Carga 34 libros de prueba
│
└── 📁 repository/
    └── BookRepository.java
        ├── findByIsbn()
        ├── findByCategory()
        ├── searchByTitleOrAuthor()
        └── findByFeatured()
```

**Endpoints**:
| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/books` | Crear libro | ✅ |
| GET | `/api/books/{id}` | Obtener libro | ✅ |
| GET | `/api/books` | Listar libros (paginado) | ✅ |
| GET | `/api/books/search?q=xxx` | Buscar libros | ✅ |
| PUT | `/api/books/{id}` | Actualizar libro | ✅ |
| DELETE | `/api/books/{id}` | Eliminar libro | ✅ |
| GET | `/api/books/{id}/availability` | Ver disponibilidad | ✅ |
| PATCH | `/api/books/{id}/copies` | Actualizar copias | ✅ |
| GET | `/api/books/category/{cat}` | Por categoría | ✅ |
| GET | `/api/books/featured` | Libros destacados | ✅ |
| GET | `/api/books/statistics` | Estadísticas | ✅ |
| POST | `/api/books/seed` | Cargar libros de prueba | ✅👑 |

---

### 3️⃣ LOAN MANAGEMENT SERVICE (Puerto 8083)

**Responsabilidad**: Préstamos, devoluciones, multas, historial

```
📁 loan-management-service/
│
├── 📁 model/
│   ├── Loan.java           → Tabla: loans
│   │   ├── id (Long)
│   │   ├── userId (Long)       → FK a users
│   │   ├── bookId (Long)       → FK a books
│   │   ├── loanDate (LocalDate)
│   │   ├── dueDate (LocalDate)
│   │   ├── returnDate (LocalDate)
│   │   ├── status (PENDING | ACTIVE | RETURNED | OVERDUE | CANCELLED)
│   │   ├── loanDays (Integer)  → Default: 14 días
│   │   ├── fineAmount (BigDecimal)
│   │   ├── extensionsCount (Integer)
│   │   └── createdAt, updatedAt
│   │
│   └── LoanHistory.java    → Tabla: loan_history
│       ├── id, loanId, action, timestamp, details
│
├── 📁 client/              → 🔗 Comunicación con otros servicios
│   ├── UserServiceClient.java      → Llama a user-service:8081
│   ├── BookServiceClient.java      → Llama a book-service:8082
│   └── NotificationServiceClient   → Llama a notification-service:8085
│
├── 📁 dto/
│   ├── LoanCreateDTO       → Entrada: {userId, bookId, loanDays}
│   ├── LoanResponseDTO     → Salida: préstamo completo
│   ├── FineCalculationDTO  → {loanId, daysOverdue, finePerDay, totalFine}
│   └── LoanValidationDTO   → Resultado de validación
│
├── 📁 service/
│   ├── LoanService.java
│   │   ├── createLoan()        → Valida usuario y libro, crea préstamo
│   │   ├── returnLoan()        → Marca como devuelto, actualiza copias
│   │   ├── extendLoan()        → Extiende fecha (máx. 2 extensiones)
│   │   ├── approveLoan()       → Admin aprueba préstamo pendiente
│   │   ├── rejectLoan()        → Admin rechaza préstamo
│   │   ├── calculateFine()     → Calcula multa por atraso
│   │   └── getOverdueLoans()   → Lista préstamos vencidos
│   │
│   └── LoanNotificationScheduler → Job que notifica vencimientos
│
└── 📁 repository/
    ├── LoanRepository.java
    │   ├── findByUserId()
    │   ├── findByBookId()
    │   ├── findByStatus()
    │   └── findOverdueLoans()
    └── LoanHistoryRepository.java
```

**Endpoints**:
| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/loans` | Solicitar préstamo | ✅ |
| GET | `/api/loans/{id}` | Obtener préstamo | ✅ |
| GET | `/api/loans` | Listar todos | ✅ |
| GET | `/api/loans/user/{userId}` | Préstamos de usuario | ✅ |
| GET | `/api/loans/user/{userId}/active` | Activos de usuario | ✅ |
| GET | `/api/loans/pending` | Pendientes de aprobar | ✅👑 |
| PUT | `/api/loans/{id}/approve` | Aprobar préstamo | ✅👑 |
| PUT | `/api/loans/{id}/reject` | Rechazar préstamo | ✅👑 |
| POST | `/api/loans/{id}/return` | Devolver libro | ✅ |
| PATCH | `/api/loans/{id}/extend` | Extender plazo | ✅ |
| PATCH | `/api/loans/{id}/cancel` | Cancelar préstamo | ✅ |
| GET | `/api/loans/overdue` | Préstamos vencidos | ✅ |
| GET | `/api/loans/{id}/fine` | Calcular multa | ✅ |
| GET | `/api/loans/{id}/history` | Historial | ✅ |

---

### 4️⃣ NOTIFICATIONS SERVICE (Puerto 8085)

**Responsabilidad**: Alertas y notificaciones a usuarios

```
📁 notifications-service/
│
├── 📁 model/
│   └── Notification.java   → Tabla: notifications
│       ├── id (Long)
│       ├── userId (Long)
│       ├── title (String)
│       ├── message (String)
│       ├── type (INFO | WARNING | ALERT | SUCCESS)
│       ├── read (Boolean)
│       └── createdAt
│
├── 📁 dto/
│   ├── NotificationCreateDTO   → Entrada: {userId, title, message, type}
│   ├── NotificationResponseDTO → Salida completa
│   └── UnreadCountResponseDTO  → {userId, unreadCount}
│
├── 📁 service/
│   └── NotificationService.java
│       ├── createNotification()
│       ├── getUserNotifications()
│       ├── markAsRead()
│       ├── markAllAsRead()
│       └── getUnreadCount()
│
└── 📁 repository/
    └── NotificationRepository.java
        ├── findByUserId()
        ├── findByUserIdAndReadFalse()
        └── countByUserIdAndReadFalse()
```

**Endpoints**:
| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/notifications` | Crear notificación | ✅ |
| GET | `/api/notifications/user/{userId}` | Notificaciones de usuario | ✅ |
| PATCH | `/api/notifications/{id}/read` | Marcar como leída | ✅ |
| PATCH | `/api/notifications/user/{userId}/read-all` | Marcar todas leídas | ✅ |
| DELETE | `/api/notifications/{id}` | Eliminar | ✅ |
| GET | `/api/notifications/user/{userId}/unread-count` | Contar no leídas | ✅ |

---

### 5️⃣ REPORTS SERVICE (Puerto 8084)

**Responsabilidad**: Dashboard y estadísticas agregadas

```
📁 reports-service/
│
├── 📁 dto/
│   └── DashboardStatisticsDTO
│       ├── totalUsers (Long)
│       ├── activeUsers (Long)
│       ├── totalBooks (Long)
│       ├── availableBooks (Long)
│       ├── activeLoans (Long)
│       ├── overdueLoans (Long)
│       └── timestamp (LocalDateTime)
│
├── 📁 service/
│   └── ReportsService.java
│       └── getDashboardStatistics()
│           → Consulta a: Users, Books, Loans
│           → Agrega datos y retorna resumen
│
└── 📁 config/
    ├── MicroservicesConfig  → URLs de otros servicios
    └── WebClientConfig      → Cliente HTTP para llamadas
```

**Endpoints**:
| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| GET | `/api/reports/dashboard` | Estadísticas del dashboard | ✅ |

---

## 🗄️ Tablas de Base de Datos

```sql
-- ===========================================
-- DATABASE: library_users_db (Puerto 3306)
-- ===========================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    role ENUM('USUARIO', 'ADMINISTRADOR') DEFAULT 'USUARIO',
    status ENUM('ACTIVO', 'BLOQUEADO') DEFAULT 'ACTIVO',
    profile_image_uri VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL
);

-- ===========================================
-- DATABASE: library_books_db
-- ===========================================

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    category VARCHAR(50),
    publisher VARCHAR(100),
    publication_year INT,
    description VARCHAR(2000),
    cover_url VARCHAR(500),
    status ENUM('AVAILABLE', 'LOANED', 'RESERVED') DEFAULT 'AVAILABLE',
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    price DECIMAL(10,2),
    featured BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- ===========================================
-- DATABASE: library_loans_db
-- ===========================================

CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('PENDING', 'ACTIVE', 'RETURNED', 'OVERDUE', 'CANCELLED') DEFAULT 'PENDING',
    loan_days INT DEFAULT 14,
    fine_amount DECIMAL(10,2) DEFAULT 0,
    extensions_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE loan_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    details VARCHAR(500),
    timestamp DATETIME NOT NULL
);

-- ===========================================
-- DATABASE: library_notifications_db
-- ===========================================

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type ENUM('INFO', 'WARNING', 'ALERT', 'SUCCESS') DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL
);
```

---

## 🔄 Flujo Completo de un Préstamo

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     FLUJO: Usuario solicita un préstamo                      │
└─────────────────────────────────────────────────────────────────────────────┘

1️⃣ USUARIO SE AUTENTICA
   ───────────────────────
   POST http://localhost:8081/api/users/login
   Body: { "email": "usuario@biblioteca.com", "password": "user123" }
   
   Response: { "token": "eyJhbGciOiJIUzI1...", "user": {...} }
   
                                    ▼

2️⃣ USUARIO VE CATÁLOGO DE LIBROS
   ─────────────────────────────────
   GET http://localhost:8082/api/books
   Headers: Authorization: Bearer eyJhbGciOiJIUzI1...
   
   Response: [{ "id": 1, "title": "Don Quijote", "availableCopies": 3 }, ...]
   
                                    ▼

3️⃣ USUARIO SOLICITA PRÉSTAMO
   ───────────────────────────
   POST http://localhost:8083/api/loans
   Headers: Authorization: Bearer eyJhbGciOiJIUzI1...
   Body: { "userId": 2, "bookId": 1, "loanDays": 14 }
   
   [LOAN SERVICE internamente]:
   ├─→ Llama a USER SERVICE: GET /api/users/2 (validar usuario existe y activo)
   ├─→ Llama a BOOK SERVICE: GET /api/books/1/availability (verificar copias)
   └─→ Crea préstamo con status: PENDING
   
   Response: { "id": 1, "status": "PENDING", "userId": 2, "bookId": 1 }
   
                                    ▼

4️⃣ ADMIN APRUEBA PRÉSTAMO
   ─────────────────────────
   PUT http://localhost:8083/api/loans/1/approve
   Headers: Authorization: Bearer [admin_token]
   
   [LOAN SERVICE internamente]:
   ├─→ Cambia status: PENDING → ACTIVE
   ├─→ Llama a BOOK SERVICE: PATCH /api/books/1/copies?change=-1 (resta copia)
   └─→ Llama a NOTIFICATION SERVICE: POST /api/notifications
       Body: { "userId": 2, "title": "Préstamo aprobado", "message": "..." }
   
   Response: { "id": 1, "status": "ACTIVE", "loanDate": "2024-01-15", "dueDate": "2024-01-29" }
   
                                    ▼

5️⃣ USUARIO DEVUELVE LIBRO
   ─────────────────────────
   POST http://localhost:8083/api/loans/1/return
   Headers: Authorization: Bearer [user_token]
   
   [LOAN SERVICE internamente]:
   ├─→ Cambia status: ACTIVE → RETURNED
   ├─→ Registra returnDate
   ├─→ Llama a BOOK SERVICE: PATCH /api/books/1/copies?change=+1 (suma copia)
   └─→ Guarda en loan_history: { action: "RETURNED", timestamp: now() }
   
   Response: { "id": 1, "status": "RETURNED", "returnDate": "2024-01-20" }
```

---

## 📡 URLs de Swagger (Documentación Interactiva)

| Servicio | URL Swagger |
|----------|-------------|
| User Management | http://localhost:8081/swagger-ui.html |
| Book Catalog | http://localhost:8082/swagger-ui.html |
| Loan Management | http://localhost:8083/swagger-ui.html |
| Reports | http://localhost:8084/swagger-ui.html |
| Notifications | http://localhost:8085/swagger-ui.html |

---

## 🧪 Usuarios de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| `admin@biblioteca.com` | `admin123` | 👑 ADMINISTRADOR |
| `usuario@biblioteca.com` | `user123` | 👤 USUARIO |

---

## 🐳 Docker Compose

Para levantar todas las bases de datos:

```bash
docker-compose up -d
```

Esto crea:
- MySQL en puerto 3306 con las 5 bases de datos

---

*Documento generado para propósitos educativos - Sistema de Biblioteca Backend*






