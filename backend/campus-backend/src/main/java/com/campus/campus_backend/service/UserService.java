package com.campus.campus_backend.service;

import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String userId, User updates) {
        User user = getUser(userId);

        user.setFirstName(updates.getFirstName());
        user.setLastName(updates.getLastName());
        user.setEmail(updates.getEmail());
        user.setPhone(updates.getPhone());
        user.setBio(updates.getBio());
        userRepo.save(user);

        return user;
    }

    public void updateProfileImage(String userId, String imageUrl) {
        User user = getUser(userId);
        user.setProfileImage(imageUrl);
        userRepo.save(user);
    }

    public void changePassword(String userId, String oldPass, String newPass) {
        User user = getUser(userId);

        if (!passwordEncoder.matches(oldPass, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect old password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPass));
        userRepo.save(user);
    }
}
