package dev.cxl.iam_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        Role role = roleMapper.toRole(request);
        role.setDeleted(false);
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream().map(role -> roleMapper.toRoleResponse(role)).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
