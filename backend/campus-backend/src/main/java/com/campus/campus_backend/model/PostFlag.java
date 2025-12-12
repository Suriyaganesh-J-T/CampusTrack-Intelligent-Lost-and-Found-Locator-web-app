package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_flags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PostFlag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private Post post;
    @ManyToOne private User reporter;

    @Column(nullable = false) private String reason;
    @Column(nullable = false) private String status = "OPEN"; // OPEN, REVIEWED, DISMISSED

    private LocalDateTime createdAt = LocalDateTime.now();
}
