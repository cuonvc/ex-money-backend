package com.exmoney.payload.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleResponseUser {

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("name")
    private String name;

    @JsonProperty("picture")
    private String avatarUrl;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("locale")
    private String locale;
}
