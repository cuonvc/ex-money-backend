package com.exmoney.payload.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RefreshTokenDto {
    private String id;
    private String token;
    private Date expireDate;
}
