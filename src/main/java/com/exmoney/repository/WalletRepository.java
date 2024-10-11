package com.exmoney.repository;

import com.exmoney.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {

    @Query("SELECT w FROM Wallet w " +
            "INNER JOIN UserWallet uw ON uw.walletId = w.id " +
            "WHERE w.id = :id " +
            "AND w.status = 'ACTIVE' " +
            "AND uw.userId = :userId")
    Optional<Wallet> findByIdAndUser(String id, String userId);

    @Query("SELECT w FROM Wallet w " +
            "INNER JOIN UserWallet uw ON uw.walletId = w.id " +
            "WHERE w.name = :name " +
            "AND w.ownerUserId = :userId " +
            "AND w.status = 'ACTIVE'")
    Optional<Wallet> findByNameOfUser(String name, String userId);

//    @Query("SELECT w FROM Wallet w " +
//            "INNER JOIN UserWallet uw ON uw.userId = :userId " +
//            "WHERE w.status = 'ACTIVE' " +
//            "AND w.ownerUserId = (CASE WHEN :isOwner = true THEN :userId ELSE w.ownerUserId END)")
//    List<Wallet> findByUserId(String userId, boolean isOwner);

    @Query("SELECT w FROM Wallet w " +
            "INNER JOIN UserWallet uw ON uw.walletId = w.id " +
            "WHERE w.status = 'ACTIVE' " +
            "AND uw.userId = :userId " +
            "AND w.ownerUserId = (CASE WHEN :isOwner = true THEN :userId ELSE w.ownerUserId END)")
    List<Wallet> findByUserId(String userId, boolean isOwner);
}
