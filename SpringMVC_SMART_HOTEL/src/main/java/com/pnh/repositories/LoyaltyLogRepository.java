package com.pnh.repositories;

import com.pnh.pojo.LoyaltyLog;
import java.util.List;

public interface LoyaltyLogRepository {
    LoyaltyLog save(LoyaltyLog log);
    List<LoyaltyLog> findByUserIdOrderByCreatedAtDesc(Long userId, int limit);
}
