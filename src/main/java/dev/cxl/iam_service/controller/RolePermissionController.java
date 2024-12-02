package dev.cxl.iam_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.entity.RolePermission;
import dev.cxl.iam_service.service.RolePermissionService;

@RestController
@RequestMapping("/role-permission")
public class RolePermissionController {
    @Autowired
    RolePermissionService rolePermissionService;

    @PostMapping
    public APIResponse<RolePermission> create(
            @RequestParam("roleCode") String roleCode,
            @RequestParam("perResource") String perResource,
            @RequestParam("perCode") String perCode) {
        return APIResponse.<RolePermission>builder()
                .result(rolePermissionService.create(roleCode, perResource, perCode))
                .build();
    }

    @PostMapping("/{roleperid}/deleted")
    public APIResponse<Boolean> deleted(@PathVariable("roleperid") String id) {
        return APIResponse.<Boolean>builder()
                .result(rolePermissionService.delete(id))
                .build();
    }
}
