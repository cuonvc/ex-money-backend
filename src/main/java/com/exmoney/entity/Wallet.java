package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

import static com.exmoney.util.Constant.Status.ACTIVE;

@Entity
@Table(name = "wallet")
@Data
public class Wallet {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.exmoney.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "status")
    private String status = ACTIVE;

    @Column(name = "is_default")
    private boolean isDefault = true;

    @Column(name = "owner_user_id")
    private String ownerUserId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
