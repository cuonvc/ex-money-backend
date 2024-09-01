package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface OAuthService {

    ResponseEntity<BaseResponse<Object>> validateGoogleToken(String token);
    ResponseEntity<BaseResponse<Object>> validateGithubCode(String code);
}
