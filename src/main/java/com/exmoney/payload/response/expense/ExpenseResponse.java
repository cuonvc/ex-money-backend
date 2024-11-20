package com.exmoney.payload.response.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    public static final String PROP_ID = "id";
    public static final String PROP_STATUS = "status";
    public static final String PROP_DESC = "description";
    public static final String PROP_AMOUNT = "amount";
    public static final String PROP_NEW_BALANCE = "newBalance";
    public static final String PROP_CURRENCY_UNIT =  "currencyUnit";
    public static final String PROP_ENTRY_DATE = "entryDate";
    public static final String PROP_ENTRY_TYPE = "entryType";
    public static final String PROP_TYPE = "type";
    public static final String PROP_WALLET_ID = "walletId";
    public static final String PROP_WALLET_NAME = "walletName";
    public static final String PROP_USER_ID = "userId";
    public static final String PROP_USER_NAME = "userName";
    public static final String PROP_CATEGORY_ID = "categoryId";
    public static final String PROP_CATEGORY_NAME = "categoryName";
    public static final String PROP_CREATED_AT = "createdAt";
    public static final String PROP_CREATED_BY = "createdBy";
    public static final String PROP_UPDATED_AT = "updatedAt";
    public static final String PROP_UPDATED_BY = "updatedBy";

    private Long id;
    private String status;
    private String description;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private String currencyUnit;
    private LocalDateTime entryDate;
    private String entryType;
    private String type;
    private Long walletId;
    private String walletName;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String updatedBy;

    public ExpenseResponse(HashMap<String, Object> response) {
        this.id = (Long) response.get(PROP_ID);
        this.status = (String) response.get(PROP_STATUS);
        this.entryDate = (LocalDateTime) response.get(PROP_ENTRY_DATE);
        this.entryType = (String) response.get(PROP_ENTRY_TYPE);
        this.description = (String) response.get(PROP_DESC);
        this.amount = (BigDecimal) response.get(PROP_AMOUNT);
        this.newBalance = (BigDecimal) response.get(PROP_NEW_BALANCE);
        this.currencyUnit = (String) response.get(PROP_CURRENCY_UNIT);
        this.type = (String) response.get(PROP_TYPE);
        this.walletId = (Long) response.get(PROP_WALLET_ID);
        this.walletName = (String) response.get(PROP_WALLET_NAME);
        this.userId = (Long) response.get(PROP_USER_ID);
        this.userName = (String) response.get(PROP_USER_NAME);
        this.categoryId = (Long) response.get(PROP_CATEGORY_ID);
        this.categoryName = (String) response.get(PROP_CATEGORY_NAME);
        this.createdAt = (LocalDateTime) response.get(PROP_CREATED_AT);
        this.createdBy = (String) response.get(PROP_CREATED_BY);
        this.updatedAt = (LocalDateTime) response.get(PROP_UPDATED_AT);
        this.updatedBy = (String) response.get(PROP_UPDATED_BY);
    }
}
