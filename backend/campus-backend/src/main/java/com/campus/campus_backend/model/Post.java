package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable=false)
    private String type; // "LOST" or "FOUND"

    @Column(nullable=false)
    private String itemName;

    private String itemType;
    private String itemModel;
    private String place;
    private LocalDate dateReported;
    private String imageUrl;

    @Column(nullable=false)
    private String status = "OPEN"; // OPEN,MATCHED,RECOVERED

    private Double lastMatchScore; // score of best match

    private LocalDateTime createdAt = LocalDateTime.now();

    // NEW FIELDS
    @Enumerated(EnumType.STRING)
    private Category category;
    // e.g. "Electronics", "Documents", "Clothing"
    @Column(length = 2000)
    private String tags;       // comma-separated tags, stored as "tag1,tag2"
}
