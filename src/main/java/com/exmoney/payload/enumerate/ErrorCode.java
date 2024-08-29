package com.exmoney.payload.enumerate;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "{internal_server_error}", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    BAD_REQUEST("BAD_REQUEST", "Request sai định dạng", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_FOUND("USER_NOT_FOUND", "Tài khoản không tồn tại", HttpStatus.NOT_FOUND.value()),
    INVALID_CREDENTIAL("INVALID_CREDENTIAL", "Tài khoản không hợp lệ", HttpStatus.FORBIDDEN.value()),
    EMAIL_EXISTED("EMAIL_EXISTED", "Email đã được đăng ký trước đó", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_NOT_MATCHED("PASSWORD_NOT_MATCHED", "Mật khẩu không khớp", HttpStatus.UNAUTHORIZED.value()),

    ;

    private String status;
    private String message;
    private int statusCode;


    ErrorCode(String status, String message, int statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
