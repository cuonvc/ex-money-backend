package com.exmoney.payload.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegRequest {

    @NotNull(message = "Tên hiển thị không được để trống")
    @NotBlank(message = "Tên hiển thị không được để trống")
    @NotEmpty(message = "Tên hiển thị không được để trống")
    @Size(min = 1, max = 14, message = "Tên phải có từ 3 đến 14 ký tự")
    private String name;

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

    @NotEmpty(message = "Mật khẩu không được để trống")
    @NotNull(message = "Mật khẩu không được để trống")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
    private String passwordConfirm;
}
