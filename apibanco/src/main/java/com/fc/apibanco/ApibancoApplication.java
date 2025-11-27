package com.fc.apibanco;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fc.apibanco.model.PasswordEncriptada;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.PasswordEncriptadaRepository;
import com.fc.apibanco.repository.UsuarioRepository;
import com.fc.apibanco.util.AESUtil;

@SpringBootApplication
public class ApibancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApibancoApplication.class, args);
    }

    @Bean
    public CommandLineRunner crearUsuarioAdmin(UsuarioRepository usuarioRepository,
                                               PasswordEncriptadaRepository passwordEncriptadaRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setEmail("admin@dominio.com");

                String passwordOriginal = "admin123";
                admin.setPasswordHash(passwordEncoder.encode(passwordOriginal));
                admin.setRol("ADMIN");
                admin.setActivo(true);

                // Valores por defecto en campos adicionales
                admin.setTeam("Default Team");
                admin.setDepartment("Default Department");
                admin.setSupervisor(null); // el admin no tiene supervisor

                usuarioRepository.save(admin);

                PasswordEncriptada pass = new PasswordEncriptada();
                pass.setUsuario(admin);
                pass.setHash(AESUtil.encrypt(passwordOriginal));
                admin.setPasswordEncriptada(pass);

                passwordEncriptadaRepository.save(pass);

                System.out.println("✅ Usuario admin creado con valores por defecto y contraseña encriptada");
            } else {
                System.out.println("ℹ️ Usuario admin ya existe");
            }
        };
    }
    
    @Bean
    public CommandLineRunner crearUsuarioSupervisor(UsuarioRepository usuarioRepository,
                                                    PasswordEncriptadaRepository passwordEncriptadaRepository,
                                                    PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByUsername("supervisor").isEmpty()) {
                Usuario supervisor = new Usuario();
                supervisor.setUsername("supervisor");
                supervisor.setFirstName("Default");
                supervisor.setLastName("Supervisor");
                supervisor.setEmail("supervisor@dominio.com");

                String passwordOriginal = "supervisor123";
                supervisor.setPasswordHash(passwordEncoder.encode(passwordOriginal));
                supervisor.setRol("SUPERVISOR");
                supervisor.setActivo(true);

                // Valores por defecto
                supervisor.setTeam("Default Team");
                supervisor.setDepartment("Default Department");
                supervisor.setSupervisor(null); // el supervisor no tiene jefe

                usuarioRepository.save(supervisor);

                PasswordEncriptada pass = new PasswordEncriptada();
                pass.setUsuario(supervisor);
                pass.setHash(AESUtil.encrypt(passwordOriginal));
                supervisor.setPasswordEncriptada(pass);

                passwordEncriptadaRepository.save(pass);

                System.out.println("✅ Usuario supervisor creado con valores por defecto y contraseña encriptada");
            } else {
                System.out.println("ℹ️ Usuario supervisor ya existe");
            }
        };
    }



}
