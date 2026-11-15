package com.jasper.convert;

import com.jasper.dto.CreateUserRequest;
import com.jasper.dto.UserResponse;
import com.jasper.pojo.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SysUser toSysUser(CreateUserRequest request);

    UserResponse toUserResponse(SysUser sysUser);
}
