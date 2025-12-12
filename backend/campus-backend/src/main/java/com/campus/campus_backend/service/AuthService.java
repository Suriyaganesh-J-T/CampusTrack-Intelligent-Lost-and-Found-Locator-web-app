package com.campus.campus_backend.service;

import com.campus.campus_backend.dto.AuthRequest;
import com.campus.campus_backend.dto.AuthResponse;
import com.campus.campus_backend.exception.EmailAlreadyExistsException;
import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.UserRepository;
import com.campus.campus_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.allowed.email.domains}")
    private String allowedDomainsCsv;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.jwtUtil = jwtUtil;
    }

    private boolean isCampusEmail(String email) {
        if (email == null || !email.contains("@")) return false;
        String domain = email.substring(email.indexOf("@"));
        Set<String> allowed = new HashSet<>(Arrays.asList(allowedDomainsCsv.split(",")));
        return allowed.contains(domain);
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? Role.STUDENT : Role.valueOf(request.getRole().toUpperCase()));
        user.setIsVerified(isCampusEmail(request.getEmail()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getUserId());
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getUserId());
    }
}
