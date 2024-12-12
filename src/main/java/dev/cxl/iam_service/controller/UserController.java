package dev.cxl.iam_service.controller;

import java.text.ParseException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.cxl.iam_service.configuration.IdpConfig;
import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.PageResponse;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.respository.custom.UserRepositoryCustom;
import dev.cxl.iam_service.service.UserService;
import dev.cxl.iam_service.service.auth.DefaultServiceImpl;
import dev.cxl.iam_service.service.storage.StorageClient;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    IdpConfig iidpConfig;

    @Autowired
    DefaultServiceImpl defaultServiceImpl;

    @Autowired
    UserRepositoryCustom userRepository;

    @Autowired
    StorageClient client;

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
    @PostMapping("/{userID}/deleted")
    APIResponse<String> deleteUser(@PathVariable("userID") String userID) {
        defaultServiceImpl.delete(userID);
        return APIResponse.<String>builder().result("thành công").build();
    }

    @GetMapping("/myInfor")
    APIResponse<UserResponse> getUser() {
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .build();
    }

    @PreAuthorize("hasPermission('USER_DATA','VIEW')")
    @GetMapping("/{userID}/infor")
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

    @PreAuthorize("hasPermission('USER_DATA','VIEW')")
    @PostMapping("/search-user")
    APIResponse<List<UserResponse>> findUserByUserName(
            @RequestParam(value = "keyWord") String keyWord,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "attribute") Object attribute,
            @RequestParam(value = "sort") String Sort) {
        return APIResponse.<List<UserResponse>>builder()
                .result(userService.findUserByKey(keyWord, page, size, attribute, Sort))
                .build();
    }

    @PreAuthorize("hasPermission('USER_DATA','VIEW')")
    @GetMapping("/search")
    public APIResponse<List<User>> getUsers(@ModelAttribute UserSearchRequest request) {
        return APIResponse.<List<User>>builder()
                .result(userRepository.search(request))
                .build();
    }

    @GetMapping("/createProfile")
    public APIResponse<Boolean> createProfile(@RequestPart("file") MultipartFile file) {
        return APIResponse.<Boolean>builder()
                .result(userService.createProfile(file))
                .build();
    }

    @GetMapping("/viewProfile")
    public ResponseEntity<?> viewProfile() {
        return userService.viewProfile();
    }
}
