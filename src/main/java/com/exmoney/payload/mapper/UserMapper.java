package com.exmoney.payload.mapper;

import com.exmoney.entity.User;
import com.exmoney.payload.request.user.ProfileRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.response.user.UserResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User regRequestToEntity(RegRequest userRequest);

    UserResponse entityToResponse(User user);

    User profileToEntity(ProfileRequest profileRequest, @MappingTarget User user);
}
