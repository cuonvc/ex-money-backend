package com.exmoney.repository;

import com.exmoney.entity.Expense;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import static com.exmoney.payload.response.expense.ExpenseResponse.*;

public interface ExpenseRepository extends JpaRepository<Expense, String> {

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.name AS " + PROP_NAME +
            ", e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.currencyUnit AS " + PROP_CURRENCY_UNIT +
            ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID + ", w.name AS " + PROP_WALLET_NAME +
            ", e.userId AS " + PROP_USER_ID + ", u1.name AS " + PROP_USER_NAME + ", e.categoryId AS " + PROP_CATEGORY_ID +
            ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT + ", u1.name AS " + PROP_CREATED_BY +
            ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "LEFT JOIN Wallet w ON e.walletId = w.id " +
            "LEFT JOIN UserWallet uw ON uw.userId = :ownerId AND uw.walletId = w.id " +
            "LEFT JOIN User u1 ON u1.id = e.userId " +
            "LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "LEFT JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.id = :id " +
            "AND (e.userId = :ownerId OR uw.userId = :ownerId)")
    Optional<ExpenseResponse> accessibleById(String id, String ownerId);

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.name AS " + PROP_NAME +
            ", e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.currencyUnit AS " + PROP_CURRENCY_UNIT +
            ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID + ", w.name AS " + PROP_WALLET_NAME +
            ", e.userId AS " + PROP_USER_ID + ", u1.name AS " + PROP_USER_NAME + ", e.categoryId AS " + PROP_CATEGORY_ID +
            ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT + ", u1.name AS " + PROP_CREATED_BY +
            ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "LEFT JOIN Wallet w ON e.walletId = w.id " +
            "LEFT JOIN UserWallet uw ON uw.userId = :userId AND uw.walletId = w.id " +
            "LEFT JOIN User u1 ON u1.id = e.userId " +
            "LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "LEFT JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.userId = :userId " +
            "AND (" +
            "   e.walletId = :walletId " +
            "   OR (" +
            "       (:walletId IS NULL OR :walletId = '') " +
            "       AND w.isDefault = true " +
            "   )" +
            ")"
    )
    List<ExpenseResponse> findAccessByUser(String userId, String walletId);
}
