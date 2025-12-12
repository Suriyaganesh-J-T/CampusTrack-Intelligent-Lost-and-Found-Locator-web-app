package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.UserRepository;
import com.campus.campus_backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    // ---------- Users ----------

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable String id, @RequestParam boolean verified) {
        return ResponseEntity.ok(adminService.setUserVerified(id, verified));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<User> setRole(@PathVariable String id, @RequestParam String role) {
        return ResponseEntity.ok(adminService.setUserRole(id, Role.valueOf(role.toUpperCase())));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ---------- Posts ----------

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> allPosts() {
        return ResponseEntity.ok(adminService.getAllPosts());
    }

    @PatchMapping("/posts/{id}/status")
    public ResponseEntity<Post> setPostStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(adminService.setPostStatus(id, status));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Flags ----------

    @PostMapping("/flags/report")
    public ResponseEntity<PostFlag> reportPost(@RequestParam String reporterId,
                                               @RequestParam Long postId,
                                               @RequestParam String reason) {
        return ResponseEntity.ok(adminService.reportPost(reporterId, postId, reason, userRepository));
    }

    @GetMapping("/flags")
    public ResponseEntity<List<PostFlag>> getFlags(@RequestParam(defaultValue = "OPEN") String status) {
        return ResponseEntity.ok(adminService.getFlags(status));
    }

    @PatchMapping("/flags/{flagId}")
    public ResponseEntity<PostFlag> resolveFlag(@PathVariable Long flagId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.resolveFlag(flagId, status));
    }

    // ---------- Analytics summary ----------

    @GetMapping("/reports/summary")
    public ResponseEntity<Map<String, Long>> getSummary() {
        Map<String, Long> map = new HashMap<>();
        map.put("totalPosts",      adminService.countTotalPosts());
        map.put("lostPosts",       adminService.countLostPosts());
        map.put("foundPosts",      adminService.countFoundPosts());
        map.put("recoveredItems",  adminService.countRecoveredPosts());
        map.put("totalUsers",      adminService.countUsers());
        map.put("totalMatches",    adminService.countMatches());
        map.put("totalMessages",   adminService.countMessages());
        return ResponseEntity.ok(map);
    }
}
