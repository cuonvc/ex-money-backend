package com.exmoney.repository;

import com.exmoney.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status NOT IN ('INACTIVE', 'DELETED')")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "INNER JOIN RefreshToken r ON r.userId = u.id " +
            "WHERE r.token = :refreshToken " +
            "AND u.status NOT IN ('INACTIVE', 'DELETED')")
    Optional<User> findByRfToken(String refreshToken);
}
