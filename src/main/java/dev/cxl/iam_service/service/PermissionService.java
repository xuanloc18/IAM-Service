package dev.cxl.iam_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.dto.request.PermissionRequest;
import dev.cxl.iam_service.dto.response.PageResponse;
import dev.cxl.iam_service.dto.response.PermissionResponse;
import dev.cxl.iam_service.entity.Permission;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.mapper.PermissionMapper;
import dev.cxl.iam_service.respository.PermissionRespository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PermissionService {

    @Autowired
    PermissionRespository permissionRespository;

    @Autowired
    PermissionMapper mapper;

    public PermissionResponse createPermission(PermissionRequest request) {
        Boolean check =
                permissionRespository.existsByResourceCodeAndScope(request.getResourceCode(), request.getScope());
        if (check) throw new AppException(ErrorCode.PERMISSION_EXISTED);
        Permission permission = mapper.toPermission(request);
        permission.setDeleted(false);
        return mapper.toPermissionResponse(permissionRespository.save(permission));
    }

    public PageResponse<PermissionResponse> getListsPer(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var data = permissionRespository.findAll(pageable);
        return PageResponse.<PermissionResponse>builder()
                .pageSize(size)
                .currentPage(page)
                .totalElements(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .data(data.getContent().stream()
                        .map(permission -> mapper.toPermissionResponse(permission))
                        .toList())
                .build();
    }

    public void delete(String id) {
        Permission permission = permissionRespository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        permission.setDeleted(true);
        permissionRespository.save(permission);
    }
}
