package com.exmoney.payload.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> implements Serializable {
    private String status; //ex: "USER_NOT_FOUND"
    private int statusCode; //ex: 404
    private String message; //tai khoan khong ton tai
    private T[] data;
    private String dateTime; //để string vì modelMapper ko map đc localDateTime
}
