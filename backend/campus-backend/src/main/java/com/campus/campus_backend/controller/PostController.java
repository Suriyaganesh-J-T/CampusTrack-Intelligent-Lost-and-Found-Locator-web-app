package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.Category;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.Post;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.PostRepository;
import com.campus.campus_backend.repository.UserRepository;
import com.campus.campus_backend.service.MatchService;
import com.campus.campus_backend.repository.MatchRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private MatchRecordRepository matchRecordRepository;


    public PostController(PostRepository postRepository,
                          UserRepository userRepository,
                          MatchService matchService,
                          MatchRecordRepository matchRecordRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.matchService = matchService;
        this.matchRecordRepository = matchRecordRepository;
    }


    // --------------------- CREATE POST ---------------------
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("type") String type,
            @RequestParam("itemName") String itemName,
            @RequestParam(value="itemType", required=false) String itemType,
            @RequestParam(value="itemModel", required=false) String itemModel,
            @RequestParam(value="place", required=false) String place,
            @RequestParam(value="dateReported", required=false) String dateReported,
            @RequestParam(value="image", required=false) MultipartFile image,
            @RequestParam(value="category", required=false) String category,
            @RequestParam(value="tags", required=false) String tags
    ) throws IOException {

        // Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            return ResponseEntity.status(401).body("Unauthorized");

        Optional<User> uOpt = userRepository.findByEmail(auth.getName());
        if (uOpt.isEmpty())
            return ResponseEntity.status(401).body("Unauthorized");

        User user = uOpt.get();

        Post p = new Post();
        p.setUser(user);
        p.setType(type.toUpperCase());
        p.setItemName(itemName);
        p.setItemType(itemType);
        p.setItemModel(itemModel);
        p.setPlace(place);

        // --- Handle ENUM Category ---
        if (category != null && !category.isBlank()) {
            try {
                p.setCategory(Category.valueOf(category.toUpperCase()));
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body("Invalid category. Allowed: " + Arrays.toString(Category.values()));
            }
        }

        // --- Save tags (simple CSV string) ---
        if (tags != null) {
            p.setTags(tags.trim().toLowerCase());
        }

        if (dateReported != null && !dateReported.isBlank()) {
            p.setDateReported(LocalDate.parse(dateReported));
        }

        // --- Handle image upload ---
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (image != null && !image.isEmpty()) {
            String ext = image.getOriginalFilename().contains(".")
                    ? image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + ext;

            Files.copy(image.getInputStream(), uploadPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            p.setImageUrl("/uploads/" + filename);
        }

        Post saved = postRepository.save(p);

        // Run auto-matching
        var matches = matchService.findMatches(saved, 5);

        if (!matches.isEmpty()) {
            saved.setLastMatchScore(matches.get(0).getValue());
            saved.setStatus("MATCHED");
            postRepository.save(saved);

            // --- Create MatchRecord entries ---
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
                record.setStatus("PENDING"); // initial status
                matchRecordRepository.save(record);

                // --- Optional: WebSocket Notification ---
                // wsService.sendToUser(matchedPost.getUser().getId(), record);
            }
        }



        Map<String, Object> response = new HashMap<>();
        response.put("post", saved);
        response.put("matches", matches);

        return ResponseEntity.ok(response);
    }

    // --------------------- SEARCH POSTS ---------------------
    @GetMapping
    public List<Post> searchPosts(
            @RequestParam(value="type", required=false) String type,
            @RequestParam(value="category", required=false) String category,
            @RequestParam(value="q", required=false) String q,
            @RequestParam(value="tags", required=false) String tags
    ) {
        Category enumCat = null;

        if (category != null) {
            enumCat = Category.valueOf(category.toUpperCase());
        }

        return postRepository.searchPosts(
                type != null ? type.toUpperCase() : null,
                enumCat,
                q,
                tags
        );
    }

    // KEEP THIS OLD ENDPOINT FOR BACKWARD COMPATIBILITY
    @GetMapping("/type/{type}")
    public List<Post> listByType(@PathVariable String type) {
        return postRepository.findByType(type.toUpperCase());
    }
}
