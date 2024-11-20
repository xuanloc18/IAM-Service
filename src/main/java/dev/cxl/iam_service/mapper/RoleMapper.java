package dev.cxl.iam_service.mapper;

import dev.cxl.iam_service.dto.request.RoleRequest;
import dev.cxl.iam_service.dto.response.RoleResponse;
import dev.cxl.iam_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target ="permissions",ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse (Role role);
}
