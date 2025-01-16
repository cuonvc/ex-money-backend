package com.exmoney.repository;

import com.exmoney.entity.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {

    @Query("SELECT dv.deviceToken FROM DeviceInfo dv " +
            "WHERE dv.userId IN :userIds " +
            "AND dv.status <> 'DELETED'")
    Set<String> findListDeviceToken(Set<Long> userIds);

    @Query("SELECT dv.deviceToken FROM DeviceInfo dv " +
            "WHERE dv.userId = :userId " +
            "AND dv.status <> 'DELETED'")
    String findDeviceTokenByUserId(Long userId);

    @Query("SELECT dv FROM DeviceInfo dv " +
            "WHERE dv.userId = :userId " +
            "AND dv.status <> 'DELETED'")
    Optional<DeviceInfo> findByUserId(Long userId);
}
