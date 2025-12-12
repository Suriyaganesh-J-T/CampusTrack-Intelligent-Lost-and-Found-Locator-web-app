package com.campus.campus_backend.service;

import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User updateProfile(String userId, User updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updates.getFirstName());
        user.setLastName(updates.getLastName());
        user.setPhone(updates.getPhone());
        user.setBio(updates.getBio());

        return userRepository.save(user);
    }

    public User uploadImage(String userId, MultipartFile file) {
        try {
            String folder = "uploads/profile/";
            Files.createDirectories(Paths.get(folder));

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + filename);

            Files.write(path, file.getBytes());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setProfileImage("/uploads/profile/" + filename);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    public void changePassword(String userId, String oldPw, String newPw) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPw, user.getPasswordHash())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }
}
