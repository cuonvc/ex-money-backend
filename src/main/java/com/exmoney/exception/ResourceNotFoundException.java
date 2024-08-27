package com.exmoney.exception;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String filedName;
    private String filedValue;

    public ResourceNotFoundException(String resourceName, String filedName, String filedValue) {
        super(String.format("%s không tồn tại với %s: %s", resourceName, filedName, filedValue));
        this.resourceName = resourceName;
        this.filedName = filedName;
        this.filedValue = filedValue;
    }
}
