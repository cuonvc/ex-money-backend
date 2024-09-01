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

    @NotNull(message = "validate.name_empty")
    @NotBlank(message = "validate.name_empty")
    @NotEmpty(message = "validate.name_empty")
    @Size(min = 3, max = 14, message = "validate.name_size")
    private String name;
}
