package dev.cxl.iam_service.mapper;

import org.mapstruct.Mapper;

import dev.cxl.iam_service.dto.request.RoleRequest;
import dev.cxl.iam_service.dto.response.RoleResponse;
import dev.cxl.iam_service.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}
