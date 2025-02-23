package com.exmoney.repository;

import com.exmoney.entity.User;
import com.exmoney.entity.UserWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    @Query("SELECT u FROM UserWallet uw " +
            "INNER JOIN User u ON u.id = uw.userId AND u.status = 'ACTIVE' " +
            "WHERE uw.walletId = :walletId " +
            "AND uw.status = 'ACTIVE'")
    List<User> findUserByWallet(Long walletId);

    @Query("SELECT uw FROM UserWallet uw " +
            "WHERE uw.userId = :userId AND uw.walletId = :walletId AND uw.status = 'ACTIVE'")
    UserWallet findByUserAndWallet(Long userId, Long walletId);

//    @Modifying
//    @Query("INSERT INTO UserWallet(userId, walletId, updatedAt, status) " +
//            "VALUES (:userId, :walletId, :updatedAt, 'ACTIVE') " +
//            "ON CONFLICT (userId, walletId, status) DO NOTHING")
//    //phải set transaction phía service đẻ rollback nếu lỗi
//    int insertIfNotExists(Long userId, Long walletId, LocalDateTime updatedAt);
}
