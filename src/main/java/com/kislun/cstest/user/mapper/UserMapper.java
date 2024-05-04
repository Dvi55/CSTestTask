package com.kislun.cstest.user.mapper;

import com.kislun.cstest.user.model.LocalUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Component
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "yyyy-MM-dd")
    LocalUser mapToLocalUser(UserBody userBody);

    void updateUserFromBody(UserBody userBody, @MappingTarget LocalUser user);

}
