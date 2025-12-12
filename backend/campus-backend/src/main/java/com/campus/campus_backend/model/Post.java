package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(nullable=false)
    private String type; // LOST or FOUND

    @Column(nullable=false)
    private String itemName;

    private String itemType;
    private String itemModel;

    @Column(name="place")
    private String place;

    private LocalDate dateReported;
    private String imageUrl;

    @Column(nullable=false)
    private String status = "OPEN"; // OPEN, MATCHED, RECOVERED

    private Double lastMatchScore;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(length = 2000)
    private String tags;
}
