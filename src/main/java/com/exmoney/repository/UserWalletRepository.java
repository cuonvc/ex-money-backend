package com.exmoney.repository;

import com.exmoney.entity.User;
import com.exmoney.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    @Query("SELECT u.name FROM UserWallet uw " +
            "INNER JOIN User u ON u.id = uw.userId AND u.status = 'ACTIVE' " +
            "WHERE uw.walletId = :walletId")
    List<String> findUserByWallet(Long walletId);
}
