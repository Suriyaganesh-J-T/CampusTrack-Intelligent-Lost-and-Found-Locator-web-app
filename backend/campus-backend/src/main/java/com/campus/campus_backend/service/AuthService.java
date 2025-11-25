package com.campus.campus_backend.service;

import com.campus.campus_backend.dto.AuthRequest;
import com.campus.campus_backend.dto.AuthResponse;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.UserRepository;
import com.campus.campus_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? "STUDENT" : request.getRole());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user); // pass full User object
        return new AuthResponse(token, user.getRole());
    }

    public AuthResponse login(AuthRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail());
        if (opt.isEmpty()) throw new RuntimeException("Invalid credentials");
        User user = opt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user); // pass full User object
        return new AuthResponse(token, user.getRole());
    }
}
