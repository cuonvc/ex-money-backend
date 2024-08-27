package com.exmoney.payload.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    @NotNull(message = "Tên hiển thị không được để trống")
    @NotBlank(message = "Tên hiển thị không được để trống")
    @NotEmpty(message = "Tên hiển thị không được để trống")
    @Size(min = 1, max = 14, message = "Tên phải có từ 3 đến 14 ký tự")
    private String name;
}
