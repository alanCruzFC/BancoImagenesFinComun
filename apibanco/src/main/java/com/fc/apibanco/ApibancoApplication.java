package com.fc.apibanco;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;


@SpringBootApplication
public class ApibancoApplication {

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
    			superAdmin.setPasswordHash(passwordEncoder.encode(initPassword)); 
    			
    			usuarioRepository.save(superAdmin); 
    			System.out.println("✅ Usuario SUPERADMIN inicial creado"); 
    		} 
    	}; 
    		
    }
}
