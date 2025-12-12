package com.campus.campus_backend.controller;

import com.campus.campus_backend.dto.*;
import com.campus.campus_backend.service.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.register(authRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
