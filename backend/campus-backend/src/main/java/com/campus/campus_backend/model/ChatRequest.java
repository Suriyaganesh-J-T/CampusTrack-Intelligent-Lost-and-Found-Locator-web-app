package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MatchRecord match;

    @ManyToOne
    private User sender; // usually founder

    @ManyToOne
    private User receiver; // usually loser

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING / ACCEPTED / DECLINED

    private LocalDateTime createdAt = LocalDateTime.now();
}
