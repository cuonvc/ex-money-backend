package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.auth.LoginRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<BaseResponse<UserResponse>> signUp(@RequestHeader(value = "Accept-Language") Locale locale,
                                                             @Valid @RequestBody RegRequest request) {
        return userService.register(request, locale);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<BaseResponse<Object>> signIn(@RequestHeader(value = "Accept-Language") Locale locale,
                                                                     @Valid @RequestBody LoginRequest request) {
        return userService.signIn(request, locale);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<BaseResponse<String>> signOut(@RequestHeader(value = "Accept-Language") Locale locale) {
        return userService.signOut(locale);
    }

    @GetMapping("/token/renew")
    public ResponseEntity<BaseResponse<Object>> renewAccessToken(@RequestParam("refresh_token") String refreshToken) {
        return userService.renewAccessToken(refreshToken);
    }
}
