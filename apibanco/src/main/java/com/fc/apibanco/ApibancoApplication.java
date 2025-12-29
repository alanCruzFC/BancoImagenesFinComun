package com.fc.apibanco;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fc.apibanco.model.PasswordEncriptada;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;
import com.fc.apibanco.util.AESUtil;


@SpringBootApplication
public class ApibancoApplication {

	Logger logger = Logger.getLogger(getClass().getName());
	
    public static void main(String[] args) {
        SpringApplication.run(ApibancoApplication.class, args);
    }

    
    @Bean
    CommandLineRunner initSuperAdmin(UsuarioRepository usuarioRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            // Si no existe el superadmin, lo creamos
            if (usuarioRepository.findByUsername("superadmin").isEmpty()) {
                Usuario superAdmin = new Usuario();
                superAdmin.setUsername("superadmin");
                superAdmin.setRol("SUPERADMIN");
                superAdmin.setActivo(true);

                // ⚠️ Usa una contraseña segura, no hardcodeada en código
                String initPassword = System.getenv("SUPERADMIN_INITIAL_PASSWORD");
                if (initPassword == null || initPassword.isBlank()) {
                    throw new IllegalStateException("Falta SUPERADMIN_INITIAL_PASSWORD en el entorno");
                }

                // Hash para login seguro (BCrypt)
                superAdmin.setPasswordHash(passwordEncoder.encode(initPassword));

                // Cifrado reversible con AESUtil
                PasswordEncriptada pass = new PasswordEncriptada();
                pass.setHash(AESUtil.encrypt(initPassword));
                pass.setUsuario(superAdmin);

                // Enlace bidireccional
                superAdmin.setPasswordEncriptada(pass);

                usuarioRepository.save(superAdmin);

                logger.info("✅ Usuario SUPERADMIN inicial creado con contraseña encriptada");
            }
        };
    }

}
