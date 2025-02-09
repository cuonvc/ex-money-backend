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
    STATUS_INVALID("STATUS_INVALID", "exception.status_invalid", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_FOUND("USER_NOT_FOUND", "exception.user_not_found", HttpStatus.NOT_FOUND.value()),
    PASSWORD_INCORRECT("PASSWORD_INCORRECT", "exception.password_incorrect", HttpStatus.BAD_REQUEST.value()),
    INVALID_CREDENTIAL("INVALID_CREDENTIAL", "exception.invalid_credential", HttpStatus.FORBIDDEN.value()),
    EMAIL_EXISTED("EMAIL_EXISTED", "exception.email_existed", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_NOT_MATCHED("PASSWORD_NOT_MATCHED", "exception.password_not_match", HttpStatus.UNAUTHORIZED.value()),

    CATEGORY_NAME_EXISTED_BY_WALLET("CATEGORY_NAME_EXISTED_BY_WALLET", "exception.category_name_existed_by_wallet", HttpStatus.BAD_REQUEST.value()),
    CATEGORY_NAME_EXISTED_BY_ACCOUNT("CATEGORY_NAME_EXISTED_BY_ACCOUNT", "exception.category_name_existed_by_account", HttpStatus.BAD_REQUEST.value()),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "exception.category_not_found", HttpStatus.NOT_FOUND.value()),
    DEFAULT_CATEGORY_CANNOT_UPDATE("DEFAULT_CATEGORY_CANNOT_UPDATE", "exception.default_category_cannot_update", HttpStatus.BAD_REQUEST.value()),

    WALLET_NOT_FOUND("WALLET_NOT_FOUND", "exception.wallet_not_found", HttpStatus.NOT_FOUND.value()),
    WALLET_NAME_ALREADY_EXISTED("WALLET_NAME_ALREADY_EXISTED", "exception.wallet_name_already_existed", HttpStatus.BAD_REQUEST.value()),
    WALLET_IN_USE_BY_USER("WALLET_IN_USE_BY_USER", "exception.wallet_in_use_by_user", HttpStatus.BAD_REQUEST.value()),
    WALLET_NOT_CONTAINS_USER("WALLET_NOT_CONTAINS_USER", "exception.wallet_not_contains_user", HttpStatus.BAD_REQUEST.value()),

    EXPENSE_NOT_FOUND("EXPENSE_NOT_FOUND", "exception.expense_not_found", HttpStatus.NOT_FOUND.value()),
    EXPENSE_IS_ACTIVE("EXPENSE_IS_ACTIVE", "exception.expense_is_active", HttpStatus.NOT_FOUND.value()),
    EXPENSE_NOT_FOUND_OR_NOT_ACCESSIBLE("EXPENSE_NOT_FOUND_OR_NOT_ACCESSIBLE", "exception.expense_not_found_or_not_accessible", HttpStatus.NOT_FOUND.value()),
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "exception.notification_not_found", HttpStatus.NOT_FOUND.value()),
    TIME_INTERVAL_TYPE_NOT_FOUND("TIME_INTERVAL_TYPE_NOT_FOUND", "exception.time_interval_type_not_found", HttpStatus.NOT_FOUND.value()),
    TIME_VALUE_NOT_MATCH_INTERVAL("TIME_VALUE_NOT_MATCH_INTERVAL", "exception.time_value_not_match_interval", HttpStatus.BAD_REQUEST.value()),
    TASK_SCHEDULER_DUPLICATED("TASK_SCHEDULER_DUPLICATED", "exception.task_scheduler_duplicated", HttpStatus.BAD_REQUEST.value()),
    TASK_SCHEDULER_NOT_FOUND("TASK_SCHEDULER_NOT_FOUND", "exception.task_scheduler_not_found", HttpStatus.NOT_FOUND.value()),
    NOTE_NOT_FOUND("NOTE_NOT_FOUND", "exception.note_not_found", HttpStatus.NOT_FOUND.value()),
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
