package dev.cxl.iam_service.controller;


import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.AuthenticationResponse;
import dev.cxl.iam_service.dto.response.IntrospectResponse;
import dev.cxl.iam_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import dev.cxl.iam_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    APIResponse<AuthenticationResponse> authenticationResponseAPIResponse(@RequestBody AuthenticationRequest request){
     var result=authenticationService.authenticate(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticationResponseAPIResponse(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result=authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
      authenticationService.logout(request);
        return APIResponse.<Void>builder()
                .build();

    }
    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> logout(@RequestBody RefreshRequest request) throws Exception {
        return APIResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();

    }
    @PostMapping("/send-email")
    APIResponse<String> sendemail (@RequestParam("email") String email){
       userService.sendotp(email);
        return APIResponse.<String>builder()
                        .result("Chuc ban thanh cong")
                        .build();
    }
    @PutMapping("/change-pass")
    APIResponse<Boolean> changePass (@RequestBody ForgotPassWord forgotPassWord){

        return APIResponse.<Boolean>builder()
                .result(userService.checkotp(forgotPassWord))
                .build();
    }




}
