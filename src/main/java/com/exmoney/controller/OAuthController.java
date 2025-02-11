package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.request.auth.OAuth2Request;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static com.exmoney.util.Constant.UserProvider.GITHUB_PROVIDER;
import static com.exmoney.util.Constant.UserProvider.GOOGLE_PROVIDER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/oauth2")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    private final ResponseFactory responseFactory;

    @Value("${exmoney.application.action_log.user_sign_in}")
    private String action_user_sign_in;

    @PostMapping("/validate")
    @Transactional
    public ResponseEntity<BaseResponse<Object>> oAuthValidateToken(@RequestBody OAuth2Request request,
                                                                   @RequestParam Locale locale) {
        log.info("triggerr - {} - {}", request.getProvider(), request.getToken());

        request.setProvider(request.getProvider().toUpperCase());
        return switch (request.getProvider()) {
            case GOOGLE_PROVIDER -> oAuthService.validateGoogleToken(request, locale);
            case GITHUB_PROVIDER -> oAuthService.validateGithubCode(request, locale);
//            case "FACEBOOK" -> oAuthService.validateFacebookToken(token);
            default -> responseFactory.fail(action_user_sign_in, HttpStatus.BAD_REQUEST, "Default case - unknown error", null);
        };
    }

}