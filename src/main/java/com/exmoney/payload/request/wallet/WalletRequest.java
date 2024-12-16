package com.exmoney.payload.request.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletRequest {

    @NotNull(message = "validate.wallet_name_empty")
    @NotEmpty(message = "validate.wallet_name_empty")
    @NotBlank(message = "validate.wallet_name_empty")
    private String name;

    private String description;
}
