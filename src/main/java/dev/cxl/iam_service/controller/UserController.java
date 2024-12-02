package dev.cxl.iam_service.controller;

import java.text.ParseException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.configuration.IdpConfig;
import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.PageResponse;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.service.UserService;
import dev.cxl.iam_service.service.auth.DefaultServiceImpl;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    IdpConfig iidpConfig;

    @Autowired
    DefaultServiceImpl defaultServiceImpl;

    @PreAuthorize("hasPermission('USER_DATA','VIEW')")
    @GetMapping
    APIResponse<PageResponse<UserResponse>> getAllUser(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "10") int size) {
        return APIResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getAllUsers(page, size))
                .build();
    }

    @PostMapping
    APIResponse<String> createUser(@RequestBody @Valid UserCreationRequest request) {
        iidpConfig.getAuthService().register(request);
        return APIResponse.<String>builder().result("").build();
    }

    @GetMapping("/confirmCreateUser")
    APIResponse<String> confirmCreateUser(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        userService.confirmCreateUser(email, otp);
        return APIResponse.<String>builder()
                .result("Đăng kí tài khoản thành công")
                .build();
    }

    @PreAuthorize("hasPermission('USER_DATA','UPDATE')")
    @PostMapping("/enable")
    APIResponse<String> enableUser(
            @RequestHeader("authorization") String token,
            @RequestParam("userID") String userID,
            @RequestBody @Valid UserUpdateRequest request)
            throws ParseException {
        iidpConfig.getAuthService().enableUser(token, userID, request);
        return APIResponse.<String>builder().result("enable thành công").build();
    }

    @PreAuthorize("hasPermission('USER_DATA','DELETE')")
    @DeleteMapping("/DeleteUser")
    APIResponse<String> deleteUser(@RequestParam("userID") String userID, @RequestBody UserUpdateRequest request) {
        defaultServiceImpl.deleteSoft(userID, request);
        return APIResponse.<String>builder().result("thành công").build();
    }

    @GetMapping("/myInfor")
    APIResponse<UserResponse> getUser() {
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .build();
    }

    @GetMapping("/Infor/{userID}")
    APIResponse<UserResponse> getinforUser(@PathVariable String userID) {
        return APIResponse.<UserResponse>builder()
                .result(userService.getInfor(userID))
                .build();
    }

    @PreAuthorize("hasPermission('USER_DATA','UPDATE')")
    @PutMapping("reset-password")
    APIResponse<String> resetPassWord(
            @RequestHeader("authorization") String token,
            @RequestParam("userID") String userID,
            @RequestBody ResetPassword request)
            throws ParseException {
        iidpConfig.getAuthService().resetPassword(token, userID, request);
        return APIResponse.<String>builder().result("thành công").build();
    }

    @PutMapping("changPass")
    APIResponse<String> changPass(@RequestBody UserRepalcePass request) {
        userService.replacePassword(request);
        return APIResponse.<String>builder().result("Chang password successful").build();
    }

    @PutMapping({"/updateInfor"})
    UserResponse updateUser(@RequestBody UserUpdateRequest request) {
        return userService.updareUser(request);
    }
}
