package com.exmoney.payload.response.expenseCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.exmoney.util.Constant.Status.ACTIVE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryResponse {

    private String id;
    private String status = ACTIVE;
    private Set<ExpenseCategoryResponse> children = new HashSet<>();
    private String iconImage; //provide by system
    private String color; //hex color code
    private String name;
    private String description;
    private String type; //DEFAULT, CUSTOM
    private String saveType;
    private String refId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String updatedBy;
}
