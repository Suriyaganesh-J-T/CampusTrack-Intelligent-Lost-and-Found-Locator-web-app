package com.campus.campus_backend.service;

import com.campus.campus_backend.model.Post;
import com.campus.campus_backend.model.Category;
import com.campus.campus_backend.repository.PostRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final PostRepository postRepository;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    // weights for different factors
    private static final double W_NAME = 0.45;
    private static final double W_CATEGORY = 0.25;
    private static final double W_TAGS = 0.20;
    private static final double W_PLACE = 0.10;
    private static final double W_DATE = 0.10;

    public MatchService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // -----------------------------
    // Helpers
    // -----------------------------
    private String safe(String s) {
        return s == null ? "" : s.toLowerCase().trim();
    }

    private double categoryScore(Post a, Post b) {
        if (a.getCategory() == null || b.getCategory() == null) return 0;
        return a.getCategory().equals(b.getCategory()) ? 1.0 : 0.0;
    }

    private double tagScore(Post a, Post b) {
        if (a.getTags() == null || b.getTags() == null) return 0;

        Set<String> tagsA = Arrays.stream(a.getTags().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> tagsB = Arrays.stream(b.getTags().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (tagsA.isEmpty() || tagsB.isEmpty()) return 0;

        long common = tagsA.stream().filter(tagsB::contains).count();
        long total = Math.max(tagsA.size(), tagsB.size());

        return (double) common / total;
    }

    private double placeScore(Post a, Post b) {
        String placeA = a.getPlace();
        String placeB = b.getPlace();
        if (placeA == null || placeB == null) return 0;
        return safe(placeA).equals(safe(placeB)) ? 1.0 : 0.0;
    }

    private double dateScore(Post a, Post b) {
        if (a.getDateReported() == null || b.getDateReported() == null) return 0;

        long days = Math.abs(ChronoUnit.DAYS.between(a.getDateReported(), b.getDateReported()));
        return days == 0 ? 1.0 : Math.max(0, 1.0 - (double) days / 30); // decay over 30 days
    }

    private double textSimilarity(String a, String b) {
        a = safe(a);
        b = safe(b);
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 1.0;
        int distance = levenshtein.apply(a, b);
        return 1.0 - ((double) distance / maxLen);
    }

    // -----------------------------
    // Main score
    // -----------------------------
    public double score(Post a, Post b) {
        double nameScore = textSimilarity(
                safe(a.getItemName()) + " " + safe(a.getItemModel()),
                safe(b.getItemName()) + " " + safe(b.getItemModel())
        );

        return Math.max(0, Math.min(1,
                W_NAME * nameScore +
                        W_CATEGORY * categoryScore(a, b) +
                        W_TAGS * tagScore(a, b) +
                        W_PLACE * placeScore(a, b) +
                        W_DATE * dateScore(a, b)
        ));
    }

    // -----------------------------
    // Find top K matches for a post
    // -----------------------------
    public List<Map.Entry<Post, Double>> findMatches(Post post, int k) {

        String oppositeType = post.getType().equalsIgnoreCase("LOST") ? "FOUND" : "LOST";
        List<Post> candidates = postRepository.findByTypeAndStatus(oppositeType, "OPEN");

        Map<Post, Double> scores = new HashMap<>();

        for (Post c : candidates) {
            double s = score(post, c);
            if (s >= 0.30) { // minimum threshold
                scores.put(c, s);
                System.out.println("Potential match: " + post.getItemName() + " <-> " + c.getItemName() + " | Score: " + s);
            }
        }

        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .collect(Collectors.toList());
    }
}
