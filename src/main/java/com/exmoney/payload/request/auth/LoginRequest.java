package com.exmoney.payload.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "Email không đúng định dạng")
    @NotNull(message = "Email không được để trống")
    @NotBlank(message = "Email không được để trống")
    @NotEmpty(message = "Email không được để trống")
    private String email;

    @NotEmpty(message = "Mật khẩu không được để trống")
    @NotNull(message = "Mật khẩu không được để trống")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
    private String password;

    private String deviceToken;
}
