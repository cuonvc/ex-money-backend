package com.exmoney.payload.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PasswordChangeRequest {

    @NotEmpty(message = "validate.password_empty")
    @NotNull(message = "validate.password_empty")
    @NotBlank(message = "validate.password_empty")
    @Size(min = 6, max = 20, message = "validate.password_size")
    private String oldPassword;

    @NotEmpty(message = "validate.password_empty")
    @NotNull(message = "validate.password_empty")
    @NotBlank(message = "validate.password_empty")
    @Size(min = 6, max = 20, message = "validate.password_size")
    private String newPassword;

    private String retypePassword;
}
