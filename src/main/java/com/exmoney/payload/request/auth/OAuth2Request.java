package com.exmoney.payload.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuth2Request {
    private String provider;
    private String token;
    private DeviceInfoRequest deviceInfo;
}
