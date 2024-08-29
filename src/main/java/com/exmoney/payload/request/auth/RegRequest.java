package com.exmoney.payload.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegRequest {

    @NotNull(message = "validate.name_empty")
    @NotBlank(message = "validate.name_empty")
    @NotEmpty(message = "validate.name_empty")
    @Size(min = 3, max = 14, message = "validate.name_size")
    private String name;

    @Email(message = "validate.email_format")
    @NotNull(message = "validate.email_empty")
    @NotBlank(message = "validate.email_empty")
    @NotEmpty(message = "validate.email_empty")
    private String email;

    @NotEmpty(message = "validate.password_empty")
    @NotNull(message = "validate.password_empty")
    @NotBlank(message = "validate.password_empty")
    @Size(min = 6, max = 20, message = "validate.password_size")
    private String password;

    @NotEmpty(message = "validate.password_empty")
    @NotNull(message = "validate.password_empty")
    @NotBlank(message = "validate.password_empty")
    @Size(min = 6, max = 20, message = "validate.password_size")
    private String passwordConfirm;
}
