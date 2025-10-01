package com.politicabr.blog.service;

import com.politicabr.blog.dto.LoginRequestDTO;
import com.politicabr.blog.dto.LoginResponseDTO;
import com.politicabr.blog.dto.RegisterRequestDTO;
import com.politicabr.blog.dto.UserDTO;
import com.politicabr.blog.entity.User;
import com.politicabr.blog.entity.enums.UserRole;
import com.politicabr.blog.exception.AuthenticationException;
import com.politicabr.blog.exception.ResourceNotFoundException;
import com.politicabr.blog.mapper.UserMapper;
import com.politicabr.blog.repository.UserRepository;
import com.politicabr.blog.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper userMapper;

    public LoginResponseDTO authenticate(LoginRequestDTO request) {
        try {
            // Buscar usuário
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Email ou senha incorretos"));

            System.out.println("Usuário encontrado: " + user.getEmail());
            System.out.println("Senha armazenada: " + user.getPassword());
            System.out.println("Senha recebida: " + request.getPassword());
            System.out.println("Senha bate: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));

            // Verificar se está ativo
            if (!user.getActive()) {
                throw new AuthenticationException("Conta desativada. Entre em contato com o administrador.");
            }

            // Debug: Verificar se a senha fornecida coincide com a armazenada
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                System.out.println("As senhas não coincidem!");
                throw new AuthenticationException("Email ou senha incorretos");
            }

            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            System.out.println("Autenticação bem-sucedida para: " + user.getEmail());

            // Gerar tokens
            String token = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            LocalDateTime expiresAt = jwtTokenProvider.getExpirationDate(token);

            System.out.println("Token: " + token);

            // Atualizar último login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Mapear usuário para DTO
            UserDTO userDTO = userMapper.toDTO(user);

            return new LoginResponseDTO(token, refreshToken, expiresAt, userDTO);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Email ou senha incorretos");
        }
    }

    public LoginResponseDTO register(RegisterRequestDTO request) {
        // Verificar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email já cadastrado");
        }

        // Verificar se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Nome de usuário já cadastrado");
        }

        // Criar novo usuário
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.READER); // Usuário começam como leitores
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Gerar tokens
        String token = jwtTokenProvider.generateToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDate(token);

        UserDTO userDTO = userMapper.toDTO(savedUser);

        return new LoginResponseDTO(token, refreshToken, expiresAt, userDTO);
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        // Validar refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        // Obter email do token
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // Buscar usuário
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Verificar se está ativo
        if (!user.getActive()) {
            throw new AuthenticationException("Conta desativada");
        }

        // Gerar novos tokens
        String newToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDate(newToken);

        UserDTO userDTO = userMapper.toDTO(user);

        return new LoginResponseDTO(newToken, newRefreshToken, expiresAt, userDTO);
    }

    public void logout(String token) {
        // Aqui você pode adicionar o token a uma blacklist no Redis
        // Por enquanto, apenas validamos
        jwtTokenProvider.validateToken(token);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
