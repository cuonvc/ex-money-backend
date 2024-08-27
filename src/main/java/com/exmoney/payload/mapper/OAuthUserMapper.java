package com.exmoney.payload.mapper;

import com.exmoney.payload.OAuthUserInfo;
import com.exmoney.payload.response.auth.GithubResponseUser;
import com.exmoney.payload.response.auth.GoogleResponseUser;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface OAuthUserMapper {

    OAuthUserInfo toUserInfo(GoogleResponseUser user);

    OAuthUserInfo toUserInfo(GithubResponseUser user);
}
