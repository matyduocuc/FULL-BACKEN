package com.library.users.config;

import com.library.users.model.User;
import com.library.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos por defecto.
 * Crea un usuario administrador si no existe.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminUserIfNotExists();
        createTestUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        String adminEmail = "admin@biblioteca.com";
        
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .name("Administrador")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMINISTRADOR)
                    .status(User.Status.ACTIVO)
                    .build();
            
            userRepository.save(admin);
            log.info("✅ Usuario admin creado: {} / admin123", adminEmail);
        } else {
            log.info("ℹ️ Usuario admin ya existe: {}", adminEmail);
        }
    }

    private void createTestUserIfNotExists() {
        String userEmail = "usuario@biblioteca.com";
        
        if (!userRepository.existsByEmail(userEmail)) {
            User user = User.builder()
                    .name("Usuario de Prueba")
                    .email(userEmail)
                    .password(passwordEncoder.encode("user123"))
                    .role(User.Role.USUARIO)
                    .status(User.Status.ACTIVO)
                    .build();
            
            userRepository.save(user);
            log.info("✅ Usuario de prueba creado: {} / user123", userEmail);
        } else {
            log.info("ℹ️ Usuario de prueba ya existe: {}", userEmail);
        }
    }
}







