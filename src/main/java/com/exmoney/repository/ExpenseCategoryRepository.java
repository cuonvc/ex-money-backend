package com.exmoney.repository;

import com.exmoney.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, String> {

    @Query("SELECT c FROM ExpenseCategory c WHERE c.id = :id AND c.status = 'ACTIVE'")
    Optional<ExpenseCategory> findById(String id);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.name = :name " +
            "AND c.saveType = 'ACCOUNT' " +
            "AND (c.refId = :userId OR c.type = 'DEFAULT') " +
            "AND c.status = 'ACTIVE'")
    Optional<ExpenseCategory> findByNameAndUserId(String name, String userId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE (c.refId = :refId AND c.saveType = :saveType) " +
            "   OR (:saveType IS NULL AND c.type = 'DEFAULT') " +
            "AND c.parentId IS NULL " +
            "AND c.status = 'ACTIVE'")
    Set<ExpenseCategory> findAllParentByRefIdAndSaveType(String refId, String saveType);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.parentId = :parentId " +
            "AND c.status = 'ACTIVE'")
    Set<ExpenseCategory> findAllByParentId(String parentId);
}
