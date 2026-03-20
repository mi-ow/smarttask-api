package com.mi_ow.smarttask.controller;

import com.mi_ow.smarttask.dto.request.LoginRequest;
import com.mi_ow.smarttask.dto.request.RegisterRequest;
import com.mi_ow.smarttask.dto.response.AuthResponse;
import com.mi_ow.smarttask.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Register and login endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user",
            description = "Create a new user account with a unique username and password")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @Operation(summary = "Login user with existing credentials",
            description = "Authenticate user and return a JWT token for subsequent requests")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
