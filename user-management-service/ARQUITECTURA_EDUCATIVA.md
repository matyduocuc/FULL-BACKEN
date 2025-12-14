# 🎓 Guía Educativa: Arquitectura del User Management Service

## 📋 Índice
1. [Mapa Conceptual Visual](#-mapa-conceptual-visual)
2. [Arquitectura en Capas](#-arquitectura-en-capas)
3. [Flujo de Datos Completo](#-flujo-de-datos-completo)
4. [Componentes Detallados](#-componentes-detallados)
5. [Ejemplos Prácticos](#-ejemplos-prácticos)
6. [Conexión con el Frontend](#-conexión-con-el-frontend)

---

## 🗺️ Mapa Conceptual Visual

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              🌐 CLIENTE (Frontend/Android/Postman)                       │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
                            ┌───────────────────────────────┐
                            │   📡 HTTP Request (JSON)      │
                            │   POST /api/users/register    │
                            │   Body: { name, email, ...}   │
                            └───────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   🔒 SECURITY LAYER                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  SecurityConfig.java         │  JwtAuthenticationFilter.java  │  JwtUtil.java   │   │
│  │  ─────────────────          │  ──────────────────────────────  │  ────────────   │   │
│  │  • Define rutas públicas    │  • Intercepta peticiones         │  • Genera JWT   │   │
│  │  • Configura autenticación  │  • Valida tokens JWT             │  • Valida JWT   │   │
│  │  • Maneja permisos de rol   │  • Establece el contexto         │  • Extrae data  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  🎮 CONTROLLER LAYER                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  UserController.java                                                             │   │
│  │  ───────────────────                                                             │   │
│  │  🔹 QUÉ HACE: Recibe peticiones HTTP y las dirige al servicio correcto          │   │
│  │  🔹 QUÉ ENTRA: DTO de entrada (UserRegistrationDTO, UserLoginDTO, etc.)         │   │
│  │  🔹 QUÉ SALE: ResponseEntity<DTO> con código HTTP (200, 201, 404, etc.)         │   │
│  │  🔹 POR QUÉ EXISTE: Separar la lógica HTTP de la lógica de negocio              │   │
│  │                                                                                  │   │
│  │  Endpoints:                                                                      │   │
│  │    POST   /api/users/register      → Crear usuario                              │   │
│  │    POST   /api/users/login         → Autenticarse                               │   │
│  │    POST   /api/users/logout        → Cerrar sesión                              │   │
│  │    GET    /api/users/{id}          → Obtener usuario                            │   │
│  │    PUT    /api/users/{id}          → Actualizar usuario                         │   │
│  │    DELETE /api/users/{id}          → Eliminar usuario                           │   │
│  │    PATCH  /api/users/{id}/block    → Bloquear/Desbloquear                       │   │
│  │    PATCH  /api/users/{id}/role     → Cambiar rol                                │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   📦 DTO LAYER                                           │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  Data Transfer Objects (DTOs)                                                    │   │
│  │  ────────────────────────────                                                    │   │
│  │  🔹 QUÉ HACE: Transporta datos entre capas sin exponer el modelo interno        │   │
│  │  🔹 POR QUÉ EXISTE: Seguridad (no exponer password), Flexibilidad, Validación   │   │
│  │                                                                                  │   │
│  │  ┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────┐  │   │
│  │  │ UserRegistrationDTO  │    │   UserResponseDTO    │    │ LoginResponseDTO │  │   │
│  │  │ ──────────────────── │    │ ────────────────── │    │ ────────────────  │  │   │
│  │  │ • name (String)      │    │ • id (Long)          │    │ • token (String) │  │   │
│  │  │ • email (String)     │    │ • name (String)      │    │ • user (DTO)     │  │   │
│  │  │ • phone (String)     │    │ • email (String)     │    │ • expiresIn      │  │   │
│  │  │ • password (String)  │    │ • role (Enum)        │    └──────────────────┘  │   │
│  │  │                      │    │ • status (Enum)      │                          │   │
│  │  │ 🚫 NO SALE password  │    │ • createdAt          │                          │   │
│  │  └──────────────────────┘    └──────────────────────┘                          │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  ⚙️ SERVICE LAYER                                        │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  UserService.java                                                                │   │
│  │  ────────────────                                                                │   │
│  │  🔹 QUÉ HACE: Contiene TODA la lógica de negocio                                │   │
│  │  🔹 QUÉ ENTRA: DTOs de entrada                                                   │   │
│  │  🔹 QUÉ SALE: DTOs de salida                                                     │   │
│  │  🔹 POR QUÉ EXISTE: Separar lógica de negocio del acceso a datos               │   │
│  │                                                                                  │   │
│  │  Transformaciones que realiza:                                                   │   │
│  │    • Encripta contraseñas con BCrypt                                            │   │
│  │    • Genera tokens JWT                                                          │   │
│  │    • Valida reglas de negocio                                                   │   │
│  │    • Convierte Entity → DTO                                                     │   │
│  │    • Maneja transacciones (@Transactional)                                      │   │
│  │                                                                                  │   │
│  │  Dependencias inyectadas:                                                        │   │
│  │    • UserRepository (acceso a datos)                                            │   │
│  │    • SessionRepository (manejo de sesiones)                                     │   │
│  │    • PasswordEncoder (encriptación)                                             │   │
│  │    • JwtUtil (tokens)                                                           │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                📚 REPOSITORY LAYER                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  UserRepository.java (Interface)                                                 │   │
│  │  ───────────────────────────────                                                 │   │
│  │  🔹 QUÉ HACE: Abstrae el acceso a la base de datos                              │   │
│  │  🔹 QUÉ ENTRA: Entities (User, Session)                                         │   │
│  │  🔹 QUÉ SALE: Entities o Optional<Entity>                                       │   │
│  │  🔹 POR QUÉ EXISTE: Desacoplar la lógica de negocio del acceso a datos         │   │
│  │                                                                                  │   │
│  │  Métodos disponibles (heredados de JpaRepository):                               │   │
│  │    • save(entity)           → Guardar/Actualizar                                │   │
│  │    • findById(id)           → Buscar por ID                                     │   │
│  │    • findAll()              → Listar todos                                      │   │
│  │    • delete(entity)         → Eliminar                                          │   │
│  │    • existsById(id)         → Verificar existencia                              │   │
│  │                                                                                  │   │
│  │  Métodos personalizados (Query Methods):                                         │   │
│  │    • findByEmail(email)     → Buscar por email                                  │   │
│  │    • existsByEmail(email)   → ¿Existe este email?                               │   │
│  │    • countByStatus(status)  → Contar por estado                                 │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  🏛️ MODEL LAYER                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  User.java (Entity)                                                              │   │
│  │  ──────────────────                                                              │   │
│  │  🔹 QUÉ HACE: Representa una fila en la tabla "users" de la BD                  │   │
│  │  🔹 POR QUÉ EXISTE: Mapeo objeto-relacional (ORM) con JPA/Hibernate             │   │
│  │                                                                                  │   │
│  │  @Entity                        ← Marca como entidad JPA                        │   │
│  │  @Table(name = "users")         ← Nombre de la tabla en BD                      │   │
│  │                                                                                  │   │
│  │  Campos:                                                                         │   │
│  │    @Id @GeneratedValue          → id (Long) - Clave primaria autoincremental   │   │
│  │    @Column(unique = true)       → email (String) - Único                        │   │
│  │    @Column(nullable = false)    → name, password - Obligatorios                 │   │
│  │    @Enumerated(EnumType.STRING) → role (USUARIO/ADMINISTRADOR)                  │   │
│  │    @Enumerated(EnumType.STRING) → status (ACTIVO/BLOQUEADO)                     │   │
│  │    @CreatedDate                 → createdAt - Fecha automática                  │   │
│  │    @LastModifiedDate            → updatedAt - Actualización automática          │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   🗄️ DATABASE                                            │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  MySQL - library_users_db                                                        │   │
│  │  ────────────────────────                                                        │   │
│  │                                                                                  │   │
│  │  Tabla: users                                                                    │   │
│  │  ┌──────────────┬──────────────┬─────────────────────────────────────────────┐  │   │
│  │  │ Columna      │ Tipo         │ Descripción                                 │  │   │
│  │  ├──────────────┼──────────────┼─────────────────────────────────────────────┤  │   │
│  │  │ id           │ BIGINT PK AI │ Identificador único                         │  │   │
│  │  │ name         │ VARCHAR(100) │ Nombre del usuario                          │  │   │
│  │  │ email        │ VARCHAR(100) │ Email único                                 │  │   │
│  │  │ phone        │ VARCHAR(20)  │ Teléfono (opcional)                         │  │   │
│  │  │ password     │ VARCHAR(255) │ Hash BCrypt de contraseña                   │  │   │
│  │  │ role         │ VARCHAR(20)  │ USUARIO o ADMINISTRADOR                     │  │   │
│  │  │ status       │ VARCHAR(20)  │ ACTIVO o BLOQUEADO                          │  │   │
│  │  │ created_at   │ DATETIME     │ Fecha de creación                           │  │   │
│  │  │ updated_at   │ DATETIME     │ Última modificación                         │  │   │
│  │  └──────────────┴──────────────┴─────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Arquitectura en Capas

### ¿Por qué capas?
Cada capa tiene una **responsabilidad única** (Principio de Responsabilidad Única - SRP):

| Capa | Responsabilidad | No debe hacer |
|------|-----------------|---------------|
| **Controller** | Recibir/enviar HTTP | Lógica de negocio |
| **Service** | Lógica de negocio | Acceso directo a BD |
| **Repository** | Acceso a datos | Lógica de negocio |
| **Model** | Representar datos | Ninguna lógica |
| **DTO** | Transportar datos | Lógica ni persistencia |
| **Config** | Configuración | Lógica de negocio |

---

## 🔄 Flujo de Datos Completo

### Ejemplo: Registro de Usuario

```
1️⃣ PETICIÓN HTTP (Cliente → Controller)
   ─────────────────────────────────────
   POST http://localhost:8081/api/users/register
   Content-Type: application/json
   
   {
     "name": "Juan Pérez",
     "email": "juan@email.com",
     "phone": "1234567890",
     "password": "secreto123"
   }

                    ▼

2️⃣ CONTROLLER RECIBE (UserController.java)
   ─────────────────────────────────────────
   @PostMapping("/register")
   public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO dto)
   
   • Valida el JSON automáticamente con @Valid
   • Convierte JSON → UserRegistrationDTO
   • Llama al servicio: userService.register(dto)

                    ▼

3️⃣ SERVICE PROCESA (UserService.java)
   ────────────────────────────────────
   public UserResponseDTO register(UserRegistrationDTO dto) {
       // 1. Validar que email no exista
       if (userRepository.existsByEmail(dto.getEmail())) {
           throw new RuntimeException("Email ya registrado");
       }
       
       // 2. Crear entidad User
       User user = User.builder()
           .name(dto.getName())
           .email(dto.getEmail())
           .password(passwordEncoder.encode(dto.getPassword())) // ⚡ ENCRIPTA
           .build();
       
       // 3. Guardar en BD
       user = userRepository.save(user);
       
       // 4. Convertir a DTO de respuesta
       return UserResponseDTO.fromEntity(user);
   }

                    ▼

4️⃣ REPOSITORY GUARDA (UserRepository.java)
   ──────────────────────────────────────────
   userRepository.save(user)
   
   • JPA genera: INSERT INTO users (name, email, password, ...) VALUES (...)
   • Retorna el User con ID generado

                    ▼

5️⃣ BASE DE DATOS
   ─────────────────
   INSERT INTO users (name, email, password, role, status, created_at)
   VALUES ('Juan Pérez', 'juan@email.com', '$2a$10$...hash...', 'USUARIO', 'ACTIVO', NOW())

                    ▼

6️⃣ RESPUESTA (Controller → Cliente)
   ─────────────────────────────────────
   HTTP 201 Created
   Content-Type: application/json
   
   {
     "id": 1,
     "name": "Juan Pérez",
     "email": "juan@email.com",
     "phone": "1234567890",
     "role": "USUARIO",
     "roleFrontend": "User",
     "status": "ACTIVO",
     "profileImageUri": null,
     "createdAt": "2024-01-15T10:30:00",
     "updatedAt": null
   }
   
   ⚠️ NOTA: ¡El password NUNCA se devuelve! (Seguridad)
```

---

## 🧩 Componentes Detallados

### 1. 📁 CONFIG (Configuración)

```java
// SecurityConfig.java - Define qué rutas son públicas/privadas
.requestMatchers("/api/users/register", "/api/users/login").permitAll()  // Públicas
.requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMINISTRADOR")     // Solo admins
.anyRequest().authenticated()                                             // Resto requiere login

// DataInitializer.java - Crea datos iniciales al arrancar
// Usuario admin por defecto: admin@biblioteca.com / admin123
// Usuario prueba: usuario@biblioteca.com / user123
```

### 2. 📁 MODEL (Entidades)

```java
// User.java - Representa la tabla "users"
@Entity                                    // "Soy una tabla de BD"
@Table(name = "users")                     // "Mi tabla se llama 'users'"
public class User {
    @Id @GeneratedValue                    // "Este campo es la clave primaria autoincremental"
    private Long id;
    
    @Column(nullable = false, unique = true)  // "Este campo es obligatorio y único"
    private String email;
    
    @Enumerated(EnumType.STRING)           // "Guarda el enum como texto, no número"
    private Role role;  // USUARIO o ADMINISTRADOR
}
```

### 3. 📁 REPOSITORY (Acceso a Datos)

```java
// UserRepository.java - Interface que JPA implementa automáticamente
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // JPA crea el SQL automáticamente basándose en el nombre del método:
    Optional<User> findByEmail(String email);   
    // → SELECT * FROM users WHERE email = ?
    
    boolean existsByEmail(String email);        
    // → SELECT COUNT(*) > 0 FROM users WHERE email = ?
    
    long countByStatus(User.Status status);     
    // → SELECT COUNT(*) FROM users WHERE status = ?
}
```

### 4. 📁 SERVICE (Lógica de Negocio)

```java
// UserService.java - Donde vive la lógica de negocio
@Service
@Transactional  // Las operaciones son atómicas (todo o nada)
public class UserService {
    
    // Inyección de dependencias
    private final UserRepository userRepository;     // Acceso a BD
    private final PasswordEncoder passwordEncoder;   // Encriptación
    private final JwtUtil jwtUtil;                   // Tokens JWT
    
    public LoginResponseDTO login(UserLoginDTO dto) {
        // 1. Buscar usuario
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        // 2. Verificar contraseña
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        // 3. Generar token JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        // 4. Retornar respuesta
        return LoginResponseDTO.builder()
            .token(token)
            .user(UserResponseDTO.fromEntity(user))
            .build();
    }
}
```

### 5. 📁 CONTROLLER (Endpoints HTTP)

```java
// UserController.java - Maneja las peticiones HTTP
@RestController                           // "Soy un controlador REST"
@RequestMapping("/api/users")             // "Mis rutas empiezan con /api/users"
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/login")                // POST /api/users/login
    @Operation(summary = "Iniciar sesión") // Documentación Swagger
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserLoginDTO dto) {
        LoginResponseDTO response = userService.login(dto);
        return ResponseEntity.ok(response);  // HTTP 200 + JSON
    }
    
    @GetMapping("/{userId}")              // GET /api/users/123
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
```

### 6. 📁 DTO (Objetos de Transferencia)

```java
// DTO de ENTRADA (lo que envía el cliente)
public class UserRegistrationDTO {
    @NotBlank(message = "El nombre es obligatorio")  // Validación
    private String name;
    
    @Email(message = "Email inválido")
    private String email;
    
    private String password;  // ⚠️ Solo en entrada, nunca en salida
}

// DTO de SALIDA (lo que devuelve el servidor)
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    // ❌ NO incluye password por seguridad
    
    // Método helper para convertir Entity → DTO
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            // ... más campos
            .build();
    }
}
```

---

## 📡 Conexión con el Frontend

### URLs de la API (Endpoints)

| Método | URL | Descripción | Autenticación |
|--------|-----|-------------|---------------|
| POST | `/api/users/register` | Registrar usuario | ❌ Pública |
| POST | `/api/users/login` | Iniciar sesión | ❌ Pública |
| POST | `/api/users/logout` | Cerrar sesión | ✅ Token |
| GET | `/api/users/{id}` | Obtener usuario | ✅ Token |
| PUT | `/api/users/{id}` | Actualizar usuario | ✅ Token |
| DELETE | `/api/users/{id}` | Eliminar usuario | ✅ Token |
| PATCH | `/api/users/{id}/block` | Bloquear usuario | ✅ Admin |
| PATCH | `/api/users/{id}/role` | Cambiar rol | ✅ Admin |
| GET | `/api/users` | Listar todos | ✅ Token |

### Cómo usar desde el Frontend

```javascript
// 1. REGISTRO (sin token)
const response = await fetch('http://localhost:8081/api/users/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Juan',
    email: 'juan@email.com',
    password: 'secreto123'
  })
});

// 2. LOGIN (sin token)
const loginResponse = await fetch('http://localhost:8081/api/users/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'juan@email.com',
    password: 'secreto123'
  })
});
const { token, user } = await loginResponse.json();

// 3. PETICIONES AUTENTICADAS (con token)
const userData = await fetch('http://localhost:8081/api/users/1', {
  headers: {
    'Authorization': `Bearer ${token}`,  // ⚡ Token JWT
    'Content-Type': 'application/json'
  }
});
```

---

## 🔐 Flujo de Autenticación JWT

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Cliente   │         │   Backend   │         │     BD      │
└──────┬──────┘         └──────┬──────┘         └──────┬──────┘
       │                       │                       │
       │  1. POST /login       │                       │
       │  {email, password}    │                       │
       │──────────────────────>│                       │
       │                       │  2. SELECT user       │
       │                       │──────────────────────>│
       │                       │                       │
       │                       │  3. user data         │
       │                       │<──────────────────────│
       │                       │                       │
       │                       │  4. Validar password  │
       │                       │  5. Generar JWT       │
       │                       │                       │
       │  6. {token, user}     │                       │
       │<──────────────────────│                       │
       │                       │                       │
       │  7. GET /users/1      │                       │
       │  Authorization: Bearer xyz                    │
       │──────────────────────>│                       │
       │                       │  8. Validar JWT       │
       │                       │  9. Extraer userId    │
       │                       │                       │
       │                       │  10. SELECT user      │
       │                       │──────────────────────>│
       │                       │                       │
       │  11. {user data}      │<──────────────────────│
       │<──────────────────────│                       │
       │                       │                       │
```

---

## 📊 Resumen de Archivos

```
user-management-service/
├── src/main/java/com/library/users/
│   ├── UserManagementServiceApplication.java  ← 🚀 Punto de entrada
│   │
│   ├── config/                                 ← ⚙️ CONFIGURACIÓN
│   │   ├── SecurityConfig.java                 │  Define seguridad
│   │   ├── JwtConfig.java                      │  Config de JWT
│   │   ├── MicroservicesConfig.java            │  URLs de otros servicios
│   │   └── DataInitializer.java                │  Datos iniciales
│   │
│   ├── controller/                             ← 🎮 CONTROLADORES
│   │   ├── UserController.java                 │  Endpoints de usuarios
│   │   └── AuditoriaController.java            │  Endpoints de auditoría
│   │
│   ├── dto/                                    ← 📦 DTOs
│   │   ├── UserRegistrationDTO.java            │  Entrada: registro
│   │   ├── UserLoginDTO.java                   │  Entrada: login
│   │   ├── UserResponseDTO.java                │  Salida: datos usuario
│   │   ├── LoginResponseDTO.java               │  Salida: token + usuario
│   │   └── ... (más DTOs)
│   │
│   ├── model/                                  ← 🏛️ ENTIDADES
│   │   ├── User.java                           │  Tabla: users
│   │   ├── Session.java                        │  Tabla: sessions
│   │   └── Auditoria.java                      │  Tabla: auditoria
│   │
│   ├── repository/                             ← 📚 REPOSITORIOS
│   │   ├── UserRepository.java                 │  CRUD de usuarios
│   │   ├── SessionRepository.java              │  CRUD de sesiones
│   │   └── AuditoriaRepository.java            │  CRUD de auditoría
│   │
│   ├── service/                                ← ⚙️ SERVICIOS
│   │   ├── UserService.java                    │  Lógica de usuarios
│   │   └── AuditoriaService.java               │  Lógica de auditoría
│   │
│   ├── security/                               ← 🔒 SEGURIDAD
│   │   ├── JwtAuthenticationFilter.java        │  Filtro de autenticación
│   │   └── JwtUtil.java                        │  Utilidades JWT
│   │
│   └── exception/                              ← ⚠️ EXCEPCIONES
│       └── GlobalExceptionHandler.java         │  Manejo de errores
│
└── src/main/resources/
    └── application.yml                         ← 📋 Configuración
```

---

## 🎯 Usuarios de Prueba

Al iniciar la aplicación, se crean automáticamente:

| Email | Password | Rol |
|-------|----------|-----|
| `admin@biblioteca.com` | `admin123` | ADMINISTRADOR |
| `usuario@biblioteca.com` | `user123` | USUARIO |

---

## 📖 Documentación Swagger

Accede a la documentación interactiva en:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8081/api-docs

---

*Documento generado para propósitos educativos - Sistema de Biblioteca*






