package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.PostFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostFlagRepository extends JpaRepository<PostFlag, Long> {
    List<PostFlag> findByStatus(String status);
}
