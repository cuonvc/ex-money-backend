package com.exmoney.repository;

import com.exmoney.entity.Notification;
import com.exmoney.payload.response.notification.NotificationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static com.exmoney.payload.response.notification.NotificationResponse.*;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT new map(n.id AS " + PROP_ID + ", n.type AS " + PROP_TYPE +
            ", n.title AS " + PROP_TITLE + ", n.content AS " + PROP_CONTENT +
            ", n.priority AS " + PROP_PRIORITY + ", n.createdAt AS " + PROP_CREATED_AT +
            ", ni.seen AS " + PROP_SEEN + ", ni.seenAt AS " + PROP_SEEN_AT +
            ") " +
            "FROM Notification n " +
            "INNER JOIN NotificationIdentity ni " +
            "   ON ni.notificationId = n.id AND ni.userId = :userId AND ni.status = 'ACTIVE' " +
            "WHERE n.status = 'ACTIVE' " +
            "ORDER BY ni.createdAt DESC, n.createdAt DESC " +
            "LIMIT :limit " +
            "OFFSET :offset")
    List<NotificationResponse> findAllByUser(Long userId, int limit, int offset);
}
