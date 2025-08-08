package com.espe.mspr.authservice;

import com.espe.mspr.authservice.model.RoleEntity;
import com.espe.mspr.authservice.model.UserEntity;
import com.espe.mspr.authservice.repository.RoleRepository;
import com.espe.mspr.authservice.repository.UserRepository;
import com.espe.mspr.authservice.user.RoleEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // 1) Crea todos los roles definidos en RoleEnum si no existen
            for (RoleEnum re : RoleEnum.values()) {
                String name = re.name();
                if (roleRepository.findByName(name).isEmpty()) {
                    RoleEntity role = RoleEntity.builder()
                            .name(name)
                            .build();
                    roleRepository.save(role);
                }
            }

            // 2) Crea un usuario admin inicial si no existe
            String adminEmail = "admin@example.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                RoleEntity adminRole = roleRepository
                        .findByName(RoleEnum.ROLE_ADMIN.name())
                        .orElseThrow();

                UserEntity admin = UserEntity.builder()
                        .nombres("Admin")
                        .apellidos("User")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("password"))
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
