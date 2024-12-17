package com.exmoney.repository;

import com.exmoney.entity.ExpenseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseHistoryRepository extends JpaRepository<ExpenseHistory, Long> {

}
