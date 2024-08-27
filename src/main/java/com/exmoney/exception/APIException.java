package com.exmoney.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
