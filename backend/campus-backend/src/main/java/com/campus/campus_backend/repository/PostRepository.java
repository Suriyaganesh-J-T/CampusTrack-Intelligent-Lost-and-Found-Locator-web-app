package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.Post;
import com.campus.campus_backend.model.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser_UserId(String userId);
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByType(String type);
    List<Post> findByTypeAndStatus(String type, String status);

    // ✅ For reporting
    long countByType(String type);
    long countByStatus(String status);
    long countByTypeAndStatus(String type, String status);

    @Query("SELECT p FROM Post p WHERE " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:q IS NULL OR LOWER(p.itemName) LIKE %:q% OR LOWER(p.itemModel) LIKE %:q%) AND " +
            "(:tags IS NULL OR LOWER(p.tags) LIKE %:tags%) " +
            "ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("type") String type,
                           @Param("category") Category category,
                           @Param("q") String q,
                           @Param("tags") String tags);
}
