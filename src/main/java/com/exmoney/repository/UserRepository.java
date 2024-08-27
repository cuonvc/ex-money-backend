package com.exmoney.repository;

import com.exmoney.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status != 'DELETE'")
    Optional<User> findByEmail(String email);
}
