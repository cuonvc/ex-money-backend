package com.exmoney.repository;

import com.exmoney.entity.Notification;
import com.exmoney.entity.NotificationIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface NotificationIdentityRepository extends JpaRepository<NotificationIdentity, Long> {

    @Query("SELECT ni FROM NotificationIdentity ni " +
            "INNER JOIN Notification n " +
            "   ON ni.notificationId = n.id AND ni.userId = :userId AND n.status = 'ACTIVE' " +
            "WHERE n.id = :id " +
            "   AND ni.status = 'ACTIVE'")
    NotificationIdentity findToUpdate(Long id, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE NotificationIdentity ni " +
            "SET ni.seen = true, ni.seenAt = :now, ni.updatedAt = :now " +
            "WHERE ni.userId = :userId")
    void remarkAllByUser(Long userId, LocalDateTime now);
}
