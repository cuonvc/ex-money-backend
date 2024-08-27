package com.exmoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserInfo {
    private String name;
    private String email;
    private String avatarUrl;
}
