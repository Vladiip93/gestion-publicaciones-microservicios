package com.espe.mspr.authservice.controller;

import com.espe.mspr.authservice.dto.CreateUserDto;
import com.espe.mspr.authservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Registra un nuevo usuario. Si dto.roles está vacío,
     * el servicio asigna ROLE_LECTOR por defecto.
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CreateUserDto dto) {
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
