package com.exmoney.payload.mapper;

import com.exmoney.entity.Wallet;
import com.exmoney.entity.WalletHistory;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.payload.response.wallet.WalletResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface WalletMapper {

    Wallet toEntity(WalletRequest request);

    @Mapping(target = "expenses", ignore = true)
    WalletResponse toResponse(Wallet wallet);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "walletId")
    WalletHistory entityToHistory(Wallet wallet);
}
