package com.exmoney.payload.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubRequestToken {
    private String clientId;
    private String clientSecret;
    private String code;
}
