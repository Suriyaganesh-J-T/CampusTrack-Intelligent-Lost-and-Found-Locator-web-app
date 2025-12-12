package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.User;
import com.campus.campus_backend.service.ProfileService;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    private final UserRepository userRepository;
    private final ProfileService profileService;

    public UserController(UserRepository userRepository,
                          ProfileService profileService) {
        this.userRepository = userRepository;
        this.profileService = profileService;
    }

    /** Get user profile */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Edit profile info */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String userId,
            @RequestBody User updates,
            Principal principal) {

        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User updated = profileService.updateProfile(userId, updates);
        return ResponseEntity.ok(updated);
    }

    /** Upload profile image */
    @PostMapping("/{userId}/upload-image")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable String userId,
            @RequestParam MultipartFile file,
            Principal principal) {

        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User updated = profileService.uploadImage(userId, file);
        return ResponseEntity.ok(updated);
    }

    /** Change password */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordChangeRequest req,
            Principal principal) {

        if (principal == null)
            return ResponseEntity.status(401).body("Unauthenticated");

        profileService.changePassword(principal.getName(), req.oldPassword, req.newPassword);
        return ResponseEntity.ok("Password updated");
    }

    public static class PasswordChangeRequest {
        public String oldPassword;
        public String newPassword;
    }
}
