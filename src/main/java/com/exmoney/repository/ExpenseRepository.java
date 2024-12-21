package com.exmoney.repository;

import com.exmoney.entity.Expense;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.exmoney.payload.response.expense.ExpenseResponse.*;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {


    @Query("SELECT e FROM Expense e " +
            "WHERE e.id = :id " +
            "AND e.createdBy = :userId " +
            "AND e.status = 'ACTIVE'")
    Expense findByIdAndOwner(Long id, Long userId);

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id " +
            "   LEFT JOIN UserWallet uw ON uw.walletId = :walletId AND uw.userId = :currentUserId AND uw.status = 'ACTIVE' " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.status <> 'DELETED' " +
            //filter by wallet
            "   AND (" +
            "       e.walletId = :walletId " +
            "       OR (" +
            "           :walletId IS NULL AND w.isDefault = true " +
            "       )" +
            "   ) " +
            //filter by keyword
            "   AND (" +
            "       :keyword IS NULL OR :keyword = '' " +
            "       OR (" +
            "           e.description LIKE CONCAT('%', :keyword, '%') " +
            "           OR CAST(e.amount AS string) LIKE CONCAT('%', :keyword, '%') " +
            "           OR EXISTS (SELECT sc.id FROM ExpenseCategory sc WHERE sc.name LIKE CONCAT('%', :keyword, '%'))" +
            "       ) " +
            "   ) " +
            //filter by category
            "   AND (" +
            "       :categoryId IS NULL " +
            "       OR e.categoryId = :categoryId " +
            "   )" +
            //filter by created by
            "   AND (" +
            "       :createdBy IS NULL " +
            "       OR e.createdBy = :createdBy " +
            "   ) " +
            "ORDER BY e.updatedAt DESC, e.id DESC "
    )
        //có thể không phải người tạo nhưng chung ví với người tạo thì vẫn xem được (nhưng không update được)
    List<ExpenseResponse> findAccessByUser(Long currentUserId, Long walletId, String keyword, Long categoryId, Long createdBy);


    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id " +
            "   INNER JOIN UserWallet uw ON uw.userId = :ownerId AND uw.walletId = w.id AND uw.status = 'ACTIVE' " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.id = :id " +
            "   AND e.status != 'DELETED' " +
            "   AND (e.userId = :ownerId OR uw.userId = :ownerId) " +
            "ORDER BY e.updatedAt DESC, e.id DESC ")
    Optional<ExpenseResponse> accessibleById(Long id, Long ownerId);

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.userId = :userId " +
            "   AND e.status != 'DELETED' " +
            "   AND EXTRACT(YEAR FROM e.createdAt) = :year " +
            "   AND EXTRACT(MONTH FROM e.createdAt) = :month " +
            "ORDER BY e.createdAt DESC"
    )
    List<ExpenseResponse> findAllByOwner(Long userId, int year, int month);
}
