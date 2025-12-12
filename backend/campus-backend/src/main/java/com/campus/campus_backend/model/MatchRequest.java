package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne @JoinColumn(name = "match_record_id", nullable = false)
    private MatchRecord match;

    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, DECLINED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
