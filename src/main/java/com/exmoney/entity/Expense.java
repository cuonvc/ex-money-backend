package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status; //ACTIVE, PENDING, REJECTED, INACTIVE, DELETED

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "new_balance")
    private BigDecimal newBalance = BigDecimal.ZERO;

    @Column(name = "entry_type")
    private String entryType; //INCOME or EXPENSE

    @Column(name = "entry_date")
    private LocalDateTime entryDate; //from client

    @Column(name = "currency_unit")
    private String currencyUnit; //VND, EUR, USD, GBP

    @Column(name = "type")
    private String type;

    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "updated_by")
    private Long updatedBy;
}
