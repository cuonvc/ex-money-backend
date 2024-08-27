package com.exmoney.payload.request.auth;

import lombok.Data;

@Data
public class TokenObjectRequest {
    private String refreshToken;
}
