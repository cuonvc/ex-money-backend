package com.exmoney.payload.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationBuilder {
    private Long identifier; //userId or groupId
    private String identifyType; //user or group
    private String priority;
    private Map<String, String> fcmData;
}
