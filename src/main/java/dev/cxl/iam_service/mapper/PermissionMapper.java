package dev.cxl.iam_service.mapper;

import dev.cxl.iam_service.dto.request.PermissionRequest;
import dev.cxl.iam_service.dto.response.PermissionResponse;
import dev.cxl.iam_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
