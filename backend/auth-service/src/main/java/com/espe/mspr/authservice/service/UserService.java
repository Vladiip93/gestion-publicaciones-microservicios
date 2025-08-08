package com.espe.mspr.authservice.service;

import com.espe.mspr.authservice.dto.CreateUserDto;
import com.espe.mspr.authservice.model.RoleEntity;
import com.espe.mspr.authservice.model.UserEntity;
import com.espe.mspr.authservice.repository.RoleRepository;
import com.espe.mspr.authservice.repository.UserRepository;
import com.espe.mspr.authservice.service.EventProducerService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventProducerService eventProducerService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EventProducerService eventProducerService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventProducerService = eventProducerService;
    }

    @Transactional
    public void createUser(CreateUserDto dto) {
        // 1) Verifico unicidad de email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // 2) Defino nombres de roles: uso ROLE_LECTOR por defecto si no hay ninguno
        Set<String> roleNames = (dto.getRoles() == null || dto.getRoles().isEmpty())
                ? Set.of("ROLE_LECTOR")
                : dto.getRoles();

        // 3) Mappeo cada nombre a la entidad RoleEntity
        Set<RoleEntity> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name))
                )
                .collect(Collectors.toSet());

        // 4) Construyo y guardo el usuario
        UserEntity u = UserEntity.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(u);

        // 5) Publico evento de registro (opcionalmente fallo sin abortar creación)
        try {
            eventProducerService.sendUserRegisteredEvent(u.getEmail());
        } catch (Exception e) {
            System.err.println("ADVERTENCIA: El usuario " + u.getEmail() +
                    " fue creado, pero falló el envío del evento de notificación.");
            e.printStackTrace();
        }
    }
}
