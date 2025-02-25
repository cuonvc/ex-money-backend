package com.exmoney.repository;

import com.exmoney.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT c FROM ExpenseCategory c WHERE c.id = :id AND c.status = 'ACTIVE'")
    Optional<ExpenseCategory> findById(Long id);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.id = :id AND c.status = 'ACTIVE' AND c.createdBy = :ownerId")
    Optional<ExpenseCategory> findByIdAndOwner(Long id, Long ownerId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.status = 'ACTIVE' " +
            "AND (" +
            "    c.type = 'DEFAULT'" +
            "    OR (" +
            "        (c.saveType = 'WALLET' AND c.refId = :walletId)" +
            "        OR (c.saveType = 'ACCOUNT' AND c.refId = :userId)" +
            "    )" +
            ")")
    List<ExpenseCategory> findAllAccessByUser(Long userId); //dùng cho search expense

    @Query("SELECT c FROM ExpenseCategory c WHERE c.name = :name AND c.type = 'DEFAULT'")
    Optional<ExpenseCategory> findDefaultByName(String name);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.name = :name " +
            "AND c.type = 'DEFAULT' " +
            "OR (c.refId = :userId AND c.saveType = 'ACCOUNT') " +
            "AND c.status = 'ACTIVE'")
    Optional<ExpenseCategory> findByNameAndUserId(String name, Long userId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.name = :name " +
            "AND c.saveType = :saveType " +
            "AND c.id <> :currentId " +
            "AND c.status = 'ACTIVE'")
    List<ExpenseCategory> findDuplicateByName(String name, String saveType, Long currentId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.name = :name " +
            "AND (c.refId = :walletId AND c.saveType = 'WALLET') " +
            "AND c.status = 'ACTIVE'")
    //còn TH category default nhưng khong decode dc
    List<ExpenseCategory> findByNameAndWalletId(String name, Long walletId);

//    @Query("SELECT c FROM ExpenseCategory c " +
//            "WHERE (" +
//            "   (c.refId = :refId AND c.saveType = :saveType) " +
//            "   OR c.type = 'DEFAULT' " +
//            ") " +
//            "AND c.parentId IS NULL " +
//            "AND c.status = 'ACTIVE'")
//    Set<ExpenseCategory> findAllParentByRefIdAndSaveType(Long refId, String saveType);

    @Query(
            "SELECT c FROM ExpenseCategory c " +
            "WHERE c.status = 'ACTIVE' " +
            "   AND c.parentId IS NULL " +
            "   AND (" +
            "       c.type = 'DEFAULT' " +
            "       OR (" +
            "           (c.saveType = 'WALLET' AND c.refId IN (" +
            "              SELECT uw.walletId FROM UserWallet uw " +
            "              WHERE uw.userId = :userId " +
            "              AND uw.walletId = :walletId " +
            "              AND uw.status = 'ACTIVE') " +
            "           ) " +
            "           OR (c.saveType = 'ACCOUNT' AND c.refId = :userId)" +
            "       )" +
            "   )"
    )
    Set<ExpenseCategory> findAllParentAccessByWallet(Long userId, Long walletId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.parentId = :parentId " +
            "AND c.status = 'ACTIVE'")
    Set<ExpenseCategory> findAllByParentId(Long parentId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.id = :id " +
            "AND c.status = 'ACTIVE' " +
            "AND (" +
            "    c.type = 'DEFAULT'" +
            "    OR (" +
            "        (c.saveType = 'WALLET' AND c.refId = :walletId)" +
            "        OR (c.saveType = 'ACCOUNT' AND c.refId = :userId)" +
            "    )" +
            ")")
    Optional<ExpenseCategory> findByIdAndAccess(Long id, Long walletId, Long userId);

    @Query("SELECT c FROM ExpenseCategory c " +
            "WHERE c.status = 'ACTIVE' " +
            "AND (" +
            "    c.type = 'DEFAULT'" +
            "    OR (" +
            "        (c.saveType = 'WALLET' AND c.refId = :walletId)" +
            "        OR (c.saveType = 'ACCOUNT' AND c.refId = :userId)" +
            "    )" +
            ")")
    List<ExpenseCategory> findAllByUserAndWallet(Long walletId, Long userId);
}
