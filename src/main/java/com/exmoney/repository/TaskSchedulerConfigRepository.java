package com.exmoney.repository;

import com.exmoney.entity.TaskSchedulerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskSchedulerConfigRepository extends JpaRepository<TaskSchedulerConfig, Long> {

    @Query("SELECT t FROM  TaskSchedulerConfig t " +
            "WHERE t.id = :id " +
            "AND t.status <> 'DELETED' " +
            "AND t.createdBy = :ownerId")
    TaskSchedulerConfig findByIdAndOwner(Long id, Long ownerId);

    @Query("SELECT t FROM TaskSchedulerConfig t " +
            "WHERE t.refId = :refId " +
            "AND t.refTable = :refTable " +
            "AND t.status <> 'DELETED'")
    TaskSchedulerConfig findByRef(Long refId, String refTable);

    @Query("SELECT t FROM TaskSchedulerConfig t " +
            "WHERE t.refTable = :refTable " +
            "AND t.taskName = :taskName " +
            "AND t.status = 'ACTIVE' ")
    List<TaskSchedulerConfig> findAllByExpenseScheduling(String refTable, String taskName);

    @Query("SELECT t FROM TaskSchedulerConfig t " +
            "WHERE t.status = 'EXECUTED'")
    List<TaskSchedulerConfig> findAllByExecuted();

    @Query("SELECT t FROM TaskSchedulerConfig t " +
            "WHERE t.refTable = :refTable " +
            "   AND t.taskName = :taskName " +
            "   AND t.timeInterval = :timeInterval " +
            "   AND t.timeValue = :timeVal " +
            "   AND t.status <> 'DELETED' " +
            "   AND t.createdBy = :userId")
    Optional<TaskSchedulerConfig> findDuplicateByOwner(String refTable, String taskName, String timeInterval, int timeVal, Long userId);

//    @Query("SELECT t FROM TaskSchedulerConfig t " +
//            "WHERE t.createdBy = :ownerId " +
//            "   AND t.status <> 'DELETED' ")
//    List<TaskSchedulerConfig> findAllByOwnerAndRef(Long ownerId, String refTable, Long refId);

    @Query("SELECT t FROM TaskSchedulerConfig t " +
            "   INNER JOIN Expense e ON e.id = t.refId " +
            "   INNER JOIN Wallet w ON w.id = e.walletId AND w.id = :walletId " +
            "WHERE t.createdBy = :ownerId " +
            "   AND t.refTable = 'expense' " +
            "   AND t.status <> 'DELETED' " +
            "ORDER BY t.updatedAt DESC, t.id DESC ")
    //find list Expense scheduler
    List<TaskSchedulerConfig> findAllByOwnerAndExpenseRef(Long ownerId, Long walletId);
}
