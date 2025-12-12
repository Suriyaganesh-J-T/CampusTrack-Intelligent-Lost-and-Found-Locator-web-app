package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.*;
import com.campus.campus_backend.security.UserDetailsImpl;
import com.campus.campus_backend.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;
    private final MatchRecordRepository matchRecordRepository;
    private final NotificationService notificationService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public PostController(PostRepository postRepository,
                          UserRepository userRepository,
                          MatchService matchService,
                          MatchRecordRepository matchRecordRepository,
                          NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.matchService = matchService;
        this.matchRecordRepository = matchRecordRepository;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam("type") String type,
                                        @RequestParam("itemName") String itemName,
                                        @RequestParam(value = "itemType", required = false) String itemType,
                                        @RequestParam(value = "itemModel", required = false) String itemModel,
                                        @RequestParam(value = "place", required = false) String place,
                                        @RequestParam(value = "dateReported", required = false) String dateReported,
                                        @RequestParam(value = "image", required = false) MultipartFile image,
                                        @RequestParam(value = "category", required = false) String category,
                                        @RequestParam(value = "tags", required = false) String tags) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // 🔥 Fixed User Identity Extraction (Uses USER ID, Not Email)
        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        String userId = principal.getUser().getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            return ResponseEntity.status(403).body("Account not verified.");
        }

        Post p = new Post();
        p.setUser(user);
        p.setType(type.toUpperCase());
        p.setItemName(itemName);
        p.setItemType(itemType);
        p.setItemModel(itemModel);
        p.setPlace(place);
        p.setStatus("OPEN");

        if (category != null && !category.isBlank()) {
            try {
                p.setCategory(Category.valueOf(category.toUpperCase()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid category.");
            }
        }

        if (tags != null) {
            p.setTags(tags.trim().toLowerCase());
        }

        if (dateReported != null && !dateReported.isBlank()) {
            p.setDateReported(LocalDate.parse(dateReported));
        }

        // 📌 Save Image Locally
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            p.setImageUrl("/uploads/" + fileName);
        }

        Post saved = postRepository.save(p);

        // 🔍 Auto Match System
        List<Map.Entry<Post, Double>> matches = matchService.findMatches(saved, 5);
        if (!matches.isEmpty()) {
            saved.setLastMatchScore(matches.get(0).getValue());
            saved.setStatus("MATCHED");
            postRepository.save(saved);

            for (var entry : matches) {
                Post matchedPost = entry.getKey();
                Double score = entry.getValue();

                MatchRecord record = new MatchRecord();
                if (saved.getType().equals("LOST")) {
                    record.setLostPost(saved);
                    record.setLostUser(saved.getUser());
                    record.setFoundPost(matchedPost);
                    record.setFoundUser(matchedPost.getUser());
                } else {
                    record.setFoundPost(saved);
                    record.setFoundUser(saved.getUser());
                    record.setLostPost(matchedPost);
                    record.setLostUser(matchedPost.getUser());
                }

                record.setMatchScore(score);
                record.setStatus("PENDING");
                MatchRecord savedRecord = matchRecordRepository.save(record);

                notificationService.notifyNewMatch(matchedPost.getUser().getUserId(), savedRecord);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("post", saved);
        response.put("matches", matches);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        String userId = principal.getUser().getUserId();

        List<Post> userPosts = postRepository.findByUser_UserId(userId);
        return ResponseEntity.ok(userPosts);
    }

    @GetMapping
    public List<Post> searchPosts(@RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "category", required = false) String category,
                                  @RequestParam(value = "q", required = false) String q,
                                  @RequestParam(value = "tags", required = false) String tags) {
        Category enumCat = null;
        if (category != null) {
            enumCat = Category.valueOf(category.toUpperCase());
        }
        return postRepository.searchPosts(type != null ? type.toUpperCase() : null, enumCat, q, tags);
    }

    @GetMapping("/type/{type}")
    public List<Post> listByType(@PathVariable String type) {
        return postRepository.findByType(type.toUpperCase());
    }


    @PostMapping("/recover/{postId}")
    public ResponseEntity<?> markRecovered(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body("Unauthorized");

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // allow ONLY owner or admin
        String currentUserId = userDetails.getUser().getUserId();
        boolean isOwner = post.getUser().getUserId().equals(currentUserId);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin)
            return ResponseEntity.status(403).body("Not allowed");

        post.setStatus("RECOVERED");
        postRepository.save(post);

        return ResponseEntity.ok("Item marked as recovered!");
    }

}
