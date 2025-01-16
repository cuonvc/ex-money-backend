package com.exmoney.payload.request.auth;

import lombok.Data;

@Data
public class DeviceInfoRequest {
    private String deviceId;
    private String deviceToken;
    private String deviceName;
    private String os;
    private String version;
}
