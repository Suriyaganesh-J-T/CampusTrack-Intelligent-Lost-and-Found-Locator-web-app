package com.campus.campus_backend.service;

import com.campus.campus_backend.dto.PostRequest;
import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.PostRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;

    public PostService(PostRepository postRepository, UserRepository userRepository, MatchService matchService) {
        this.postRepository = postRepository; this.userRepository = userRepository; this.matchService = matchService;
    }

    public Post createPost(PostRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (Boolean.FALSE.equals(user.getIsVerified())) {
            throw new RuntimeException("Account not verified");
        }

        Post post = new Post();
        post.setUser(user);
        post.setType(request.getType().toUpperCase());
        post.setItemName(request.getItemName());
        post.setItemType(request.getItemType());
        post.setItemModel(request.getItemModel());
        post.setPlace(request.getPlace());
        post.setDateReported(request.getDateReported());
        post.setImageUrl(request.getImageUrl());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags() != null ? request.getTags().trim().toLowerCase() : null);
        post.setStatus("OPEN");
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        List<Map.Entry<Post, Double>> potentialMatches = matchService.findMatches(savedPost, 5);
        if (!potentialMatches.isEmpty()) {
            savedPost.setStatus("MATCHED");
            savedPost.setLastMatchScore(potentialMatches.get(0).getValue());
            postRepository.save(savedPost);
        }

        for (Map.Entry<Post, Double> entry : potentialMatches) {
            Post matchedPost = entry.getKey();
            Double score = entry.getValue();

            MatchRecord matchRecord = new MatchRecord();
            if (savedPost.getType().equals("LOST")) {
                matchRecord.setLostPost(savedPost);
                matchRecord.setLostUser(savedPost.getUser());
                matchRecord.setFoundPost(matchedPost);
                matchRecord.setFoundUser(matchedPost.getUser());
            } else {
                matchRecord.setFoundPost(savedPost);
                matchRecord.setFoundUser(savedPost.getUser());
                matchRecord.setLostPost(matchedPost);
                matchRecord.setLostUser(matchedPost.getUser());
            }
            matchRecord.setMatchScore(score);
            matchRecord.setStatus("PENDING");
            matchRecord.setCreatedAt(LocalDateTime.now());

            matchService.saveMatchRecord(matchRecord);
        }

        return savedPost;
    }
}
