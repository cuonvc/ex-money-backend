package com.exmoney.repository;

import com.exmoney.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUserId(String userId);

    Optional<RefreshToken> findByToken(String token);
}
