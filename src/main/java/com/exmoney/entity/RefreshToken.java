package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.exmoney.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "expire_date")
    private Date expireDate;

    @Column(name = "user_id")
    private String userId;
}
