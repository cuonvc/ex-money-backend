package com.exmoney.entity;

import com.exmoney.payload.enumerate.Role;
import com.exmoney.payload.enumerate.Status;
import com.exmoney.payload.enumerate.UserProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

import static com.exmoney.util.Utils.getNow;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class User {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.exmoney.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "role")
    private String role = Role.USER.name();

    @Column(name = "user_provider")
    private String userProvider = UserProvider.SYSTEM.name();

    @Column(name = "status")
    private String status = Status.ACTIVE.name();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = getNow();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
