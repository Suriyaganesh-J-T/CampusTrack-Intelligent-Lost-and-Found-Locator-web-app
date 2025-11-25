package com.campus.campus_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // optional link to MatchRecord if you use it
    @ManyToOne(fetch = FetchType.LAZY)
    private MatchRecord match;

    @ManyToOne
    private User user1; // lost user

    @ManyToOne
    private User user2; // found user

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE / CLOSED

    private LocalDateTime createdAt = LocalDateTime.now();
}
