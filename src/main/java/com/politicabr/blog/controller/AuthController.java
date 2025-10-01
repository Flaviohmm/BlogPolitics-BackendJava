package com.politicabr.blog.controller;

import com.politicabr.blog.dto.LoginRequestDTO;
import com.politicabr.blog.dto.LoginResponseDTO;
import com.politicabr.blog.dto.RefreshTokenRequestDTO;
import com.politicabr.blog.dto.RegisterRequestDTO;
import com.politicabr.blog.entity.User;
import com.politicabr.blog.entity.enums.UserRole;
import com.politicabr.blog.repository.UserRepository;
import com.politicabr.blog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, String>> createAdmin() {
        // Deletar se existir
        userRepository.findByEmail("admin@politicabr.com")
                .ifPresent(user -> userRepository.delete(user));

        // Criar novo com senha encodada pelo MESMO passwordEncoder da aplicação
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@politicabr.com");
        admin.setPassword(passwordEncoder.encode(""));
        admin.setFirstName("Administrador");
        admin.setLastName("Sistema");
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        admin.setEmailVerified(true);
        admin.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(admin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin criado com sucesso!");
        response.put("email", saved.getEmail());
        response.put("hash", saved.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        LoginResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        LoginResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        boolean isValid = authService.validateToken(jwtToken);
        return ResponseEntity.ok(isValid);
    }
}
