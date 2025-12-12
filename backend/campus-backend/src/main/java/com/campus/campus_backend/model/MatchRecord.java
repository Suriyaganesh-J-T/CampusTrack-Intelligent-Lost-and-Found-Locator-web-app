package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MatchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private Post lostPost;
    @ManyToOne private Post foundPost;

    @ManyToOne private User lostUser;
    @ManyToOne private User foundUser;

    private Double matchScore;

    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();
}
