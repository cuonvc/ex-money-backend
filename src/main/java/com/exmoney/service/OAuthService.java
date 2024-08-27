package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.response.auth.TokenObjectResponse;
import org.springframework.http.ResponseEntity;

public interface OAuthService {

    ResponseEntity<BaseResponse<TokenObjectResponse>> validateGoogleToken(String token);
    ResponseEntity<BaseResponse<TokenObjectResponse>> validateGithubCode(String code);
}
