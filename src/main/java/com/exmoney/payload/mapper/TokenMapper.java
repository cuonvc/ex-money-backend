package com.exmoney.payload.mapper;

import com.exmoney.entity.RefreshToken;
import com.exmoney.payload.dto.RefreshTokenDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface TokenMapper {

    RefreshTokenDto mapToDto(RefreshToken refreshToken);
}
