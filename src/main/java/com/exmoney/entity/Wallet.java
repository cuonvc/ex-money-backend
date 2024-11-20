package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.exmoney.util.Constant.Status.ACTIVE;

@Entity
@Table(name = "wallet")
@Data
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status = ACTIVE;

    @Column(name = "is_default")
    private Boolean isDefault = true;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "total_income")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "total_expense")
    private BigDecimal totalExpense = BigDecimal.ZERO;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
