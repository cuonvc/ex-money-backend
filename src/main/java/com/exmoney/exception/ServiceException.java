package com.exmoney.exception;

import com.exmoney.payload.enumerate.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;
import static com.exmoney.util.Utils.getNowStr;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    private String status;
    private int statusCode;
    private Object[] args;
    private String dateTime; //để string vì modelMapper ko map đc localDateTime

    public ServiceException(ErrorCode error) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.statusCode = error.getStatusCode();
        this.dateTime = getNowStr();
    }

    public ServiceException(ErrorCode error, Object... args) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.statusCode = error.getStatusCode();
        this.args = args == null ? new Object[0] : args;
        this.dateTime = getNowStr();
    }

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
