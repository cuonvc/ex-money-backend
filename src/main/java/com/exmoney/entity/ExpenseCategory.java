package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

import static com.exmoney.util.Constant.Status.ACTIVE;

@Entity
@Table(name = "expense_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategory {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.exmoney.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "status")
    private String status = ACTIVE;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "ref_id")
    private String refId; //userId or walletId

    @Column(name = "save_type")
    private String saveType; //WALLET, ACCOUNT

    @Column(name = "icon_image")
    private String iconImage; //provide by system

    @Column(name = "color")
    private String color; //hex color code

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type; //DEFAULT, CUSTOM

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "updated_by")
    private String updatedBy;
}
