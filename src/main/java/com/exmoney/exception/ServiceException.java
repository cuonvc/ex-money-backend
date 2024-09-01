package com.exmoney.exception;

import com.exmoney.payload.enumerate.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import static com.exmoney.util.Utils.getNow;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    private String status;
    private int statusCode;
    private Object[] args;
    private LocalDateTime dateTime;

    public ServiceException(String message, String status, int statusCode, Object... args) {
        super(message);
        this.status = status;
        this.statusCode = statusCode;
        this.args = args;
        this.dateTime = getNow();
    }

//    public ServiceException(ErrorCode error, Object... args) {
//        super();
//        this.status = error.getStatus();
//        this.statusCode = error.getStatusCode();
//        this.args = args == null ? new Object[0] : args;
//        this.dateTime = getNowStr();
//    }

    public Object[] getArgs() {
        return args == null ? new Object[0] : args;
    }

    @Override
    public String getMessage() {
        if (args != null) {
            return MessageFormat.format(super.getMessage(), args);
        }
        return super.getMessage();
    }
}
