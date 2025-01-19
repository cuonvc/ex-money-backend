package com.exmoney.repository;

import com.exmoney.entity.NotificationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationGroupRepository extends JpaRepository<NotificationGroup, Long> {

    @Query("SELECT g FROM NotificationGroup g " +
            "WHERE g.id = :id")
    NotificationGroup findEntityById(long id);
}
