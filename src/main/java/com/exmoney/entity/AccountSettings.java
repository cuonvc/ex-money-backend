package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSettings {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.exmoney.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
