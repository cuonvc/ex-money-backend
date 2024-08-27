package com.exmoney.payload.response.auth;

import com.exmoney.payload.dto.RefreshTokenDto;
import com.exmoney.payload.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenObjectResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private RefreshTokenDto refreshToken;
    private UserResponse userResponse;
}
