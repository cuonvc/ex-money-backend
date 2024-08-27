package com.exmoney.payload.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubResponseToken {
    private String access_token;
    private String scope;
    private String token_type;
}
