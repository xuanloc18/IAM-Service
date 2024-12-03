package dev.cxl.iam_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.entity.UserRole;
import dev.cxl.iam_service.service.UserRoleService;

@RestController
@RequestMapping("user-role")
public class UserRoleController {
    @Autowired
    UserRoleService userRoleService;

    @PostMapping
    public APIResponse<UserRole> createUserRole(
            @RequestParam("userMail") String userMail, @RequestParam("roleCode") String roleCode) {
        return APIResponse.<UserRole>builder()
                .result(userRoleService.createUserRole(userMail, roleCode))
                .build();
    }

    @PostMapping("{userroleid}/deleted")
    public APIResponse<Boolean> delete(@PathVariable("userroleid") String id) {
        return APIResponse.<Boolean>builder().result(userRoleService.delete(id)).build();
    }
}
