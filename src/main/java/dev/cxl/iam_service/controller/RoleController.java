package dev.cxl.iam_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.dto.request.RoleRequest;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.PageResponse;
import dev.cxl.iam_service.dto.response.RoleResponse;
import dev.cxl.iam_service.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    RoleService roleService;

    //    @PreAuthorize("hasPermission('ROLE_DATA','CREATE')")
    @PostMapping
    APIResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @PreAuthorize("hasPermission('ROLE_DATA','VIEW')")
    @GetMapping
    APIResponse<PageResponse<RoleResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "10") int size) {
        return APIResponse.<PageResponse<RoleResponse>>builder()
                .result(roleService.getAll(page, size))
                .build();
    }

    @PreAuthorize("hasPermission('ROLE_DATA','DELETE')")
    @PostMapping("/{role}/deleted")
    APIResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return APIResponse.<Void>builder().build();
    }
}
