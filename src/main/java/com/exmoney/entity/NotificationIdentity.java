package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_identity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_type")
    private String identityType; //USER, GROUP

    @Column(name = "identity_id")
    private Long identityId; //userId or userGroup

    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "seen")
    private boolean seen;

    @Column(name = "seen_at")
    private LocalDateTime seenAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
