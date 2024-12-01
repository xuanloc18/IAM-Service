package dev.cxl.iam_service.service;

import java.util.List;

import dev.cxl.iam_service.dto.response.PageResponse;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.dto.request.RoleRequest;
import dev.cxl.iam_service.dto.response.RoleResponse;
import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.mapper.RoleMapper;
import dev.cxl.iam_service.respository.RoleRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        Boolean check=roleRepository.existsByCode(request.getCode());
        if(check)
            throw new AppException(ErrorCode.ROLE_EXISTED);
        Role role = roleMapper.toRole(request);
        role.setDeleted(false);
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public PageResponse<RoleResponse> getAll(int page,int size) {
        Pageable pageable= PageRequest.of(page-1,size);
        var roles = roleRepository.findAll(pageable);
        return PageResponse.<RoleResponse>builder()
                .totalPages(roles.getTotalPages())
                .currentPage(page)
                .totalElements(roles.getTotalElements())
                .pageSize(size)
                .data(roles.getContent().stream().map(role -> roleMapper.toRoleResponse(role)).toList())
                .build();

    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
