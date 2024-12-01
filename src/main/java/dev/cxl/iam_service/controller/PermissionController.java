package dev.cxl.iam_service.controller;

import java.util.List;

import dev.cxl.iam_service.dto.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.dto.request.PermissionRequest;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.PermissionResponse;
import dev.cxl.iam_service.service.PermissionService;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    PermissionService permissionService;

    @PostMapping
    APIResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    APIResponse<PageResponse<PermissionResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "10") int size)
     {
        return APIResponse.<PageResponse<PermissionResponse>>builder()
                .result(permissionService.getListsPer(page,size))
                .build();
    }

    @DeleteMapping("/{permission}")
    APIResponse<Void> delete(@PathVariable String permission) {
        permissionService.deletePermission(permission);
        return APIResponse.<Void>builder().build();
    }
}
