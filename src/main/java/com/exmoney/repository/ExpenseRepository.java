package com.exmoney.repository;

import com.exmoney.entity.Expense;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.overview.WeekMapAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.exmoney.payload.response.expense.ExpenseResponse.*;
import static com.exmoney.payload.response.overview.WeekMapAmount.PROP_DATE;
import static com.exmoney.payload.response.overview.WeekMapAmount.PROP_WEEK;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {


    @Query("SELECT e FROM Expense e " +
            "WHERE e.id = :id " +
            "AND e.createdBy = :userId " +
            "AND e.status IN ('ACTIVE', 'SCHEDULED')")
    Expense findByIdToUpdate(Long id, Long userId);

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , COALESCE(cParent.id, c.id) AS " + PROP_PARENT_CATEGORY_ID + ", COALESCE(cParent.name, c.name) AS " + PROP_PARENT_CATEGORY_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id AND w.status = 'ACTIVE' " +
            "   INNER JOIN UserWallet uw ON uw.walletId = e.walletId AND uw.status = 'ACTIVE' " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "   LEFT JOIN ExpenseCategory cParent ON c.parentId = cParent.id AND cParent.status = 'ACTIVE' " +
            "WHERE e.status = 'ACTIVE' " +
            "   AND uw.userId = :currentUserId " +
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
            //filter by range time
            "   AND (" +
            "       (cast(:startDate as DATE) IS NULL OR cast(:endDate as DATE) IS NULL) " +
            "       OR (e.entryDate BETWEEN :startDate AND :endDate) " +
            "   )" +
            "ORDER BY e.updatedAt DESC, e.id DESC " +
            "LIMIT :limit OFFSET :offset"
    )
        //có thể không phải người tạo nhưng chung ví với người tạo thì vẫn xem được (nhưng không update được)
    List<ExpenseResponse> findAccessByUser(Long currentUserId, Long walletId, String keyword, Long categoryId, Long createdBy, LocalDateTime startDate, LocalDateTime endDate, int offset, int limit);


    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , COALESCE(cParent.id, c.id) AS " + PROP_PARENT_CATEGORY_ID + ", COALESCE(cParent.name, c.name) AS " + PROP_PARENT_CATEGORY_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id AND w.status = 'ACTIVE' " +
            "   INNER JOIN UserWallet uw ON uw.userId = :ownerId AND uw.walletId = w.id AND uw.status = 'ACTIVE' " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "   LEFT JOIN ExpenseCategory cParent ON c.parentId = cParent.id AND cParent.status = 'ACTIVE' " +
            "WHERE e.id = :id " +
            "   AND e.status = :status " +
            "   AND (e.userId = :ownerId OR uw.userId = :ownerId) " +
            "ORDER BY e.updatedAt DESC, e.id DESC ")
    Optional<ExpenseResponse> accessibleById(Long id, Long ownerId, String status);

    @Query("SELECT new map (e.id AS " + PROP_ID + ", e.status AS " + PROP_STATUS + ", e.entryDate AS " + PROP_ENTRY_DATE +
            "   , e.entryType AS " + PROP_ENTRY_TYPE + ", c.iconImage AS " +PROP_CATEGORY_ICON_IMAGE +
            "   , e.description AS " + PROP_DESC + ", e.amount AS " + PROP_AMOUNT + ", e.newBalance AS " + PROP_NEW_BALANCE +
            "   , e.currencyUnit AS " + PROP_CURRENCY_UNIT + ", e.type AS " + PROP_TYPE + ", e.walletId AS " + PROP_WALLET_ID +
            "   , w.name AS " + PROP_WALLET_NAME +
            "   , e.categoryId AS " + PROP_CATEGORY_ID + ", c.name AS " + PROP_CATEGORY_NAME + ", e.createdAt AS " + PROP_CREATED_AT +
            "   , u1.name AS " + PROP_CREATED_BY + ", e.updatedAt AS " + PROP_UPDATED_AT + ", u2.name AS " + PROP_UPDATED_BY + ") " +
            "FROM Expense e " +
            "   INNER JOIN Wallet w ON e.walletId = w.id AND w.status = 'ACTIVE' " +
            "   INNER JOIN User u1 ON u1.id = e.userId " +
            "   LEFT JOIN User u2 ON u2.id = e.updatedBy " +
            "   INNER JOIN ExpenseCategory c ON c.id = e.categoryId " +
            "WHERE e.userId = :userId " +
            "   AND e.status = 'ACTIVE' " +
            "   AND EXTRACT(YEAR FROM e.entryDate) = :year " +
            "   AND EXTRACT(MONTH FROM e.entryDate) = :month " +
            "ORDER BY e.createdAt DESC " +
            "LIMIT :limit OFFSET :offset"
    )
    List<ExpenseResponse> findAllByOwner(Long userId, int year, int month, int offset, int limit);

    @Query("SELECT " +
            "   SUM(CASE" +
            "   WHEN EXTRACT(YEAR FROM e.entryDate) = :year " +
            "       AND EXTRACT(MONTH FROM e.entryDate) = :month " +
            "       AND EXTRACT(DAY FROM e.entryDate) <= :dayOfMonth " +
            "   THEN e.amount " +
            "   ELSE 0 " +
            "   END ), " +

            "   SUM(CASE" +
            "   WHEN (CASE WHEN :month = 1 THEN (EXTRACT(YEAR FROM e.entryDate) = (:year - 1)) ELSE (EXTRACT(YEAR FROM e.entryDate) = :year) END) " +
            "       AND (CASE WHEN :month = 1 THEN (EXTRACT(MONTH FROM e.entryDate) = 12) ELSE (EXTRACT(MONTH FROM e.entryDate) = (:month - 1)) END) " +
            "       AND EXTRACT(DAY FROM e.entryDate) <= :dayOfMonth " +
            "   THEN e.amount " +
            "   ELSE 0 " +
            "   END ) " +
            "FROM Expense e " +
            "INNER JOIN Wallet w ON w.id = e.walletId AND w.status = 'ACTIVE' " +
            "WHERE e.userId = :userId " +
            "   AND e.status = 'ACTIVE' ")
    Object compareWithPrevMonth(Long userId, int year, int month, int dayOfMonth);

    @Query("SELECT e FROM Expense e " +
            "INNER JOIN Wallet w ON w.id = e.walletId AND w.status = 'ACTIVE' " +
            "WHERE e.status = 'SCHEDULED' " +
            "AND e.id = :id " +
            "AND e.type = 'SCHEDULE'")
    Optional<Expense> findByIdScheduled(Long id);

//    @Query("SELECT new map ( " +
//            "   DATE_TRUNC('WEEK', e.entryDate) AS " + PROP_DATE +
//            "   ,0 AS " + PROP_WEEK + //map later
//            "   ,SUM(e.amount) AS " + PROP_AMOUNT + ") " +
//            "FROM Expense e " +
//            "   INNER JOIN User u ON u.id = e.userId " +
//            "WHERE e.userId = :ownerId " +
//            "   AND (DATE(e.entryDate) BETWEEN DATE(:startDate) AND DATE(:endDate))" +
//            "   AND e.status != 'DELETE' " +
//            "GROUP BY " + PROP_DATE + " " +
//            "ORDER BY " + PROP_DATE)
//    List<WeekMapAmount> findEachWeekByOwner(Long ownerId, LocalDate startDate, LocalDate endDate);
}
