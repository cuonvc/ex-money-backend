package com.exmoney.payload.mapper;

import com.exmoney.entity.Wallet;
import com.exmoney.payload.request.wallet.WalletRequest;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface WalletMapper {

    Wallet toEntity(WalletRequest request);
}
