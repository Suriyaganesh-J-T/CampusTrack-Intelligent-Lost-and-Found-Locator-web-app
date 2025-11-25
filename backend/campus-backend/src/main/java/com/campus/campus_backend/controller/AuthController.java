package com.campus.campus_backend.controller;

import com.campus.campus_backend.dto.AuthRequest;
import com.campus.campus_backend.dto.AuthResponse;
import com.campus.campus_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest req) {
        AuthResponse res = authService.register(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        AuthResponse res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is working";
    }

}
