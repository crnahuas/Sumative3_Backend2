package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class InitialDataConfig {

    @Bean
    CommandLineRunner createInitialAdmin(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${minimarket.admin.username:admin}") String username,
            @Value("${minimarket.admin.password:admin123}") String password) {
        return args -> {
            Rol adminRole = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> rolRepository.save(new Rol("ROLE_ADMIN")));

            if (usuarioRepository.findByUsername(username).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername(username);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRoles(Set.of(adminRole));
                usuarioRepository.save(admin);
            }
        };
    }
}
