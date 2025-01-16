package com.exmoney.repository;

import com.exmoney.entity.NotificationIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationIdentityRepository extends JpaRepository<NotificationIdentity, Long> {
}
