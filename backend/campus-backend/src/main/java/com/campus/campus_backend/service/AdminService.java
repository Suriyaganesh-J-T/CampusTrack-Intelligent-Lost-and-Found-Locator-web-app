package com.campus.campus_backend.service;

import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostFlagRepository postFlagRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AdminService(UserRepository userRepository,
                        PostRepository postRepository,
                        PostFlagRepository postFlagRepository,
                        MatchRecordRepository matchRecordRepository,
                        ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postFlagRepository = postFlagRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    // ---------- User Management ----------

    public User setUserVerified(String userId, boolean verified) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        u.setIsVerified(verified);
        return userRepository.save(u);
    }

    public User setUserRole(String userId, Role role) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        u.setRole(role);
        return userRepository.save(u);
    }

    // ---------- Posts & Flags ----------

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post setPostStatus(Long postId, String status) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        p.setStatus(status);
        return postRepository.save(p);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public PostFlag reportPost(String reporterId, Long postId, String reason, UserRepository userRepo) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostFlag flag = new PostFlag();
        flag.setReporter(reporter);
        flag.setPost(post);
        flag.setReason(reason);
        flag.setStatus("OPEN");
        return postFlagRepository.save(flag);
    }

    public List<PostFlag> getFlags(String status) {
        return postFlagRepository.findByStatus(status);
    }

    public PostFlag resolveFlag(Long flagId, String status) {
        PostFlag pf = postFlagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Flag not found"));
        pf.setStatus(status);
        return postFlagRepository.save(pf);
    }

    // ---------- Analytics counts ----------

    public long countTotalPosts() { return postRepository.count(); }
    public long countLostPosts()  { return postRepository.countByType("LOST"); }
    public long countFoundPosts() { return postRepository.countByType("FOUND"); }

    // “MATCHED” = system found a pair, “RECOVERED” = item successfully closed
    public long countMatchedPosts()   { return postRepository.countByStatus("MATCHED"); }
    public long countRecoveredPosts() { return postRepository.countByStatus("RECOVERED"); }

    public long countUsers()          { return userRepository.count(); }
    public long countMatches()        { return matchRecordRepository.count(); }
    public long countMessages()       { return chatMessageRepository.count(); }
}
