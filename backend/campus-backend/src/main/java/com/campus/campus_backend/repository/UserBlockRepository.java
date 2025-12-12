package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    boolean existsByBlocker_UserIdAndBlocked_UserId(String blockerId, String blockedId);
}
