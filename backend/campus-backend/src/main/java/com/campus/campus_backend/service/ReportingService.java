package com.campus.campus_backend.service;

import com.campus.campus_backend.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportingService {

    private final PostRepository postRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ReportingService(PostRepository postRepository,
                            MatchRecordRepository matchRecordRepository,
                            ChatMessageRepository chatMessageRepository,
                            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> summary() {
        Map<String, Object> out = new HashMap<>();
        long totalPosts = postRepository.count();
        long lostPosts = postRepository.countByType("LOST");
        long foundPosts = postRepository.countByType("FOUND");
        long recoveredItems = postRepository.countByStatus("RECOVERED");

        out.put("totalPosts", totalPosts);
        out.put("lostPosts", lostPosts);
        out.put("foundPosts", foundPosts);
        out.put("recoveredItems", recoveredItems);
        out.put("totalMatches", matchRecordRepository.count());
        out.put("messages", chatMessageRepository.count());
        out.put("totalUsers", userRepository.count());

        return out;
    }
}
