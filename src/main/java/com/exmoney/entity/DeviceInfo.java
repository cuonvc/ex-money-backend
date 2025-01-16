package com.exmoney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.exmoney.util.Constant.DeviceStatus.ACTIVE;

@Entity
@Table(name = "device_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeviceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "user_id")
    private Long userId; //nếu login máy khác thì logout máy trước

    @Column(name = "os")
    private String os;

    @Column(name = "version")
    private String version;

    @Column(name = "status")
    private String status = ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
