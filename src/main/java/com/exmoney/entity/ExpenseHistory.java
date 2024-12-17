package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.exmoney.util.Constant.Status.INACTIVE;

@Entity
@Table(name = "expense_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "status")
    private String status = INACTIVE;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "new_balance")
    private BigDecimal newBalance;

    @Column(name = "entry_type")
    private String entryType;

    @Column(name = "entry_date")
    private LocalDateTime entryDate;

    @Column(name = "currency_unit")
    private String currencyUnit;

    @Column(name = "type")
    private String type;

    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
