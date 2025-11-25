package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.Category;
import com.campus.campus_backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByType(String type);

    // ⭐ REQUIRED for MatchService
    List<Post> findByTypeAndStatus(String type, String status);

    @Query("""
        SELECT p FROM Post p 
        WHERE (:type IS NULL OR p.type = :type)
        AND (:category IS NULL OR p.category = :category)
        AND (
            :q IS NULL OR 
            LOWER(p.itemName) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.itemType) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.itemModel) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.tags) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        AND (
            :tag IS NULL OR 
            LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))
        )
    """)
    List<Post> searchPosts(
            @Param("type") String type,
            @Param("category") Category category,
            @Param("q") String q,
            @Param("tag") String tag
    );
}
