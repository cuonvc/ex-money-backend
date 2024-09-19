package com.exmoney.payload.common;

import com.exmoney.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.exmoney.util.Constant.ActionBy.ACTION_BY_USER;
import static com.exmoney.util.Utils.getNow;

@Component
@RequiredArgsConstructor
public class ResponseFactory {

    private final CommonService commonService;

    public <T> ResponseEntity<BaseResponse<T>> success(String log, String messageCode, Locale locale, T... data) {
        commonService.actionLog(log, ACTION_BY_USER, HttpStatus.OK.value());
        BaseResponse response = BaseResponse.<T>builder()
                .code(0)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .message(commonService.getMessageSrc(messageCode, locale))
                .data(data)
                .dateTime(getNow())
                .build();

        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<BaseResponse<T>> success(String log, T... data) {
        commonService.actionLog(log, ACTION_BY_USER, HttpStatus.OK.value());
        BaseResponse response = BaseResponse.<T>builder()
                .code(0)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(data)
                .dateTime(getNow())
                .build();

        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<BaseResponse<T>> fail(String log, HttpStatus status, String message, T... data) {
        commonService.actionLog(log, ACTION_BY_USER, status.value());
        BaseResponse response = BaseResponse.<T>builder()
                .code(1)
                .status(HttpStatus.BAD_REQUEST.name())
                .statusCode(status.value())
                .message(message == null ? "FAILED" : message)
                .data(data)
                .dateTime(getNow())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
