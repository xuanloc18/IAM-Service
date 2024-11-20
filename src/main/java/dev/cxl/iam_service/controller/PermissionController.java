package dev.cxl.iam_service.controller;


import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.dto.request.PermissionRequest;
import dev.cxl.iam_service.dto.response.PermissionResponse;
import dev.cxl.iam_service.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    PermissionService permissionService;
    @PostMapping
    APIResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
    return APIResponse.<PermissionResponse>builder()
            .result(permissionService.createPermission(request))
            .build();
    }

    @GetMapping
    APIResponse<List<PermissionResponse>> getAll(){
        return APIResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getListsPer())
                .build();
    }
    @DeleteMapping("/{permission}")
    APIResponse<Void> delete(@PathVariable String permission){
    permissionService.deletePermission(permission);
        return APIResponse.<Void>builder().build();

    }





}
