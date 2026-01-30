package com.fc.apibanco;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;

@SpringBootApplication
public class ApibancoApplication {

    Logger logger = Logger.getLogger(getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(ApibancoApplication.class, args);
    }

    @Bean
    CommandLineRunner initSuperAdmin(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByUsername("superadmin").isEmpty()) {
                Usuario superAdmin = new Usuario();
                superAdmin.setUsername("superadmin");
                superAdmin.setRol("SUPERADMIN");
                superAdmin.setActivo(true);

                String initPassword = System.getenv("SUPERADMIN_INITIAL_PASSWORD");
                if (initPassword == null || initPassword.isBlank()) {
                    throw new IllegalStateException("Falta SUPERADMIN_INITIAL_PASSWORD en el entorno");
                }

                superAdmin.setPasswordHash(passwordEncoder.encode(initPassword));

                usuarioRepository.save(superAdmin);

                logger.info("✅ Usuario SUPERADMIN inicial creado con contraseña encriptada");
            }
        };
    }

}
