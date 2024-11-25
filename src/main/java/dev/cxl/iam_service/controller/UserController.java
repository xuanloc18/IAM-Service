package dev.cxl.iam_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserRepalcePass;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    APIResponse<List<UserResponse>> getAllUser() {
        return APIResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @PostMapping
    APIResponse<String> createUser(@RequestBody @Valid UserCreationRequest request) {
        userService.createUser(request);
        return APIResponse.<String>builder()
                .result("Mời bạn vào email xác thực đăng kí")
                .build();
    }

    @GetMapping("/confirmCreateUser")
    APIResponse<String> confirmCreateUser(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        userService.confirmCreateUser(email, otp);
        return APIResponse.<String>builder()
                .result("Đăng kí tài khoản thành công")
                .build();
    }

    @GetMapping("/DeleteUser")
    APIResponse<String> deleteUser(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        userService.del(email, otp);
        return APIResponse.<String>builder().result("Hủy đăng kí thành công").build();
    }

    @GetMapping("/myInfor")
    APIResponse<UserResponse> getUser() {
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .build();
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
