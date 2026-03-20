package com.mi_ow.smarttask.service;

import com.mi_ow.smarttask.dto.request.LoginRequest;
import com.mi_ow.smarttask.dto.request.RegisterRequest;
import com.mi_ow.smarttask.dto.response.AuthResponse;
import com.mi_ow.smarttask.entity.User;
import com.mi_ow.smarttask.repository.UserRepository;
import com.mi_ow.smarttask.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("mi_ow");
        mockUser.setEmail("mi_ow@example.com");
        mockUser.setPassword("encodedPassword");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("mi_ow");
        registerRequest.setEmail("mi_ow@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("mi_ow");
        loginRequest.setPassword("password123");
    }

    // ── Register Tests ───────────────────────────────────────────

    @Test
    void register_Success() {
        when(userRepository.existsByUsername("mi_ow")).thenReturn(false);
        when(userRepository.existsByEmail("mi_ow@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken("mi_ow")).thenReturn("mockToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mi_ow", response.getUsername());
        assertEquals("mockToken", response.getToken());
        assertEquals("Registration successful", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenUsernameExists() {
        when(userRepository.existsByUsername("mi_ow")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByUsername("mi_ow")).thenReturn(false);
        when(userRepository.existsByEmail("mi_ow@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ── Login Tests ──────────────────────────────────────────────

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("mi_ow")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken("mi_ow")).thenReturn("mockToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mi_ow", response.getUsername());
        assertEquals("mockToken", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_ThrowsException_WhenUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("mi_ow")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("User not found", exception.getMessage());
    }
}