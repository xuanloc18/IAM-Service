package dev.cxl.iam_service.controller;

import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserRepalcePass;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    APIResponse<UserResponse> createUser(@PathParam("avata") String avatar, @RequestBody @Valid UserCreationRequest request){

     return APIResponse.<UserResponse>builder()
             .result(userService.createUser(request))
             .build();
    }
    @GetMapping("/myInfor")
    APIResponse<UserResponse> getUser(){
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .build();

    }
    @PutMapping("changPass")
    APIResponse<String> changPass(@RequestBody UserRepalcePass request){
        userService.replacePassword(request);
        return APIResponse.<String>builder()
                .result("Chang password successful")
                .build();
    }

    @PutMapping({"/updateInfor"})
    UserResponse updateUser(@RequestBody UserUpdateRequest request){
        return  userService.updareUser(request);
    }
    @DeleteMapping({"/{userID}"})
    String deleteUser(@PathVariable("userID") String userID){
        userService.del(userID);
        return "User had deleted";
    }
}
