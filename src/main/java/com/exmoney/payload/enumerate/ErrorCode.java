package com.exmoney.payload.enumerate;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    RESOURCE_DO_NOT_ACCESS("RESOURCE_DO_NOT_ACCESS", "exception.resource_do_no_access", HttpStatus.FORBIDDEN.value()),
    JWT_INVALID_SIGNATURE("JWT_INVALID_SIGNATURE", "exception.jwt.invalid_signature", HttpStatus.UNAUTHORIZED.value()),
    JWT_INVALID_TOKEN("JWT_INVALID_TOKEN", "exception.jwt.invalid_token", HttpStatus.UNAUTHORIZED.value()),
    JWT_EXPIRED_TOKEN("JWT_EXPIRED_TOKEN", "exception.jwt.expired_token", HttpStatus.UNAUTHORIZED.value()),
    JWT_UNSUPPORTED_TOKEN("JWT_UNSUPPORTED_TOKEN", "exception.jwt.unsupported_token", HttpStatus.UNAUTHORIZED.value()),
    JWT_CLAIM_IS_EMPTY("JWT_CLAIM_IS_EMPTY", "exception.jwt.claim_empty", HttpStatus.UNAUTHORIZED.value()),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "exception.internal_server", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    BAD_REQUEST("BAD_REQUEST", "exception.bad_request", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_FOUND("USER_NOT_FOUND", "exception.user_not_found", HttpStatus.NOT_FOUND.value()),
    PASSWORD_INCORRECT("PASSWORD_INCORRECT", "exception.password_incorrect", HttpStatus.BAD_REQUEST.value()),
    INVALID_CREDENTIAL("INVALID_CREDENTIAL", "exception.invalid_credential", HttpStatus.FORBIDDEN.value()),
    EMAIL_EXISTED("EMAIL_EXISTED", "exception.email_existed", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_NOT_MATCHED("PASSWORD_NOT_MATCHED", "exception.password_not_match", HttpStatus.UNAUTHORIZED.value()),

    CATEGORY_NAME_EXISTED("CATEGORY_NAME_EXISTED", "exception.category_name_existed", HttpStatus.BAD_REQUEST.value()),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "exception.category_not_found", HttpStatus.NOT_FOUND.value()),
    CATEGORY_DEFAULT_CANNOT_UPDATE("CATEGORY_DEFAULT_CANNOT_UPDATE", "exception.category_default_cannot_update", HttpStatus.BAD_REQUEST.value()),

    WALLET_NOT_FOUND("WALLET_NOT_FOUND", "exception.wallet_not_found", HttpStatus.NOT_FOUND.value()),
    WALLET_NAME_ALREADY_EXISTED("WALLET_NAME_ALREADY_EXISTED", "exception.wallet_name_already_existed", HttpStatus.BAD_REQUEST.value()),
    WALLET_IN_USE_BY_USER("WALLET_IN_USE_BY_USER", "exception.wallet_in_use_by_user", HttpStatus.BAD_REQUEST.value()),
    ;

    private String status;
    private String messageCode;
    private int statusCode;


    ErrorCode(String status, String messageCode, int statusCode) {
        this.status = status;
        this.messageCode = messageCode;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
