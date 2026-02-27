package com.pnh.repositories;

import com.pnh.pojo.PasswordResetToken;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    PasswordResetToken findByToken(String token);
    void deleteByToken(String token);
    void deleteExpired();
}
