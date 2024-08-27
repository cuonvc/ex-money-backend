package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.auth.PasswordChangeRequest;
import com.exmoney.payload.request.user.ProfileRequest;
import com.exmoney.payload.response.user.PageResponseUsers;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.exmoney.util.Constant.PageConstant.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/account/edit")
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(@Valid @RequestBody ProfileRequest request) {
        return userService.editProfile(request);
    }

    @PutMapping("/password-change")
    public ResponseEntity<BaseResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        return userService.changePassword(request);
    }

    @GetMapping("/account")
    public ResponseEntity<BaseResponse<UserResponse>> getProfile() {
        return userService.getAccount();
    }

    @GetMapping("/moderator/account-list")
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAllAccount(@RequestParam(value = "pageNo",
                                                                                defaultValue = PAGE_NO, required = false) Integer pageNo,
                                                                         @RequestParam(value = "pageSize",
                                                                                 defaultValue = PAGE_SIZE, required = false) Integer pageSize,
                                                                         @RequestParam(value = "sortBy",
                                                                                 defaultValue = SORT_BY, required = false) String sortBy,
                                                                         @RequestParam(value = "sortDir",
                                                                                 defaultValue = SORT_DIR, required = false) String sortDir) {
        return userService.getAll(pageNo, pageSize, sortBy, sortDir);
    }

    @PutMapping("/account/avatar")
    public ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(@RequestPart(name = "image") MultipartFile file) {
        return userService.uploadAvatar(file);
    }
}
