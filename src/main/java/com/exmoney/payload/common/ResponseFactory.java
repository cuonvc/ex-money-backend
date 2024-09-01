package com.exmoney.payload.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.exmoney.util.Utils.getNow;

@Component
public class ResponseFactory {
    public <T> ResponseEntity<BaseResponse<T>> success(String message, T... data) {
        BaseResponse response = BaseResponse.<T>builder()
                .code(0)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .dateTime(getNow())
                .build();

        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<BaseResponse<T>> success(T... data) {
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

    public <T> ResponseEntity<BaseResponse<T>> fail(HttpStatus status, String message, T... data) {
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
