package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.auth.LoginRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.response.auth.TokenObjectResponse;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<BaseResponse<UserResponse>> signUp(@Valid @RequestBody RegRequest request) {
        return userService.register(request);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<BaseResponse<TokenObjectResponse>> signIn(@Valid @RequestBody LoginRequest request) {
        return userService.signIn(request);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<BaseResponse<String>> signOut() {
        return userService.signOut();
    }

    @GetMapping("/token/renew")
    public ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(@RequestParam("refresh_token") String refreshToken) {
        return userService.renewAccessToken(refreshToken);
    }
}
