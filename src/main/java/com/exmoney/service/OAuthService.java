package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.auth.OAuth2Request;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface OAuthService {

    ResponseEntity<BaseResponse<Object>> validateGoogleToken(OAuth2Request request, Locale locale);
    ResponseEntity<BaseResponse<Object>> validateGithubCode(OAuth2Request request, Locale locale);
}
