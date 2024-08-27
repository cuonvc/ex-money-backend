package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.auth.LoginRequest;
import com.exmoney.payload.request.auth.PasswordChangeRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.request.user.ProfileRequest;
import com.exmoney.payload.response.user.PageResponseUsers;
import com.exmoney.payload.response.auth.TokenObjectResponse;
import com.exmoney.payload.response.user.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    ResponseEntity<BaseResponse<UserResponse>> register(RegRequest request);

    ResponseEntity<BaseResponse<TokenObjectResponse>> signIn(LoginRequest request);

    ResponseEntity<BaseResponse<String>> signOut();

    ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(String refreshToken);

    ResponseEntity<BaseResponse<UserResponse>> editProfile(ProfileRequest request);

    ResponseEntity<BaseResponse<String>> changePassword(PasswordChangeRequest request);

    ResponseEntity<BaseResponse<UserResponse>> getAccount();

    ResponseEntity<BaseResponse<PageResponseUsers>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(MultipartFile file) ;
}
