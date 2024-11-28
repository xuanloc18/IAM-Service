package dev.cxl.iam_service.controller;

import java.io.IOException;
import java.text.ParseException;

import dev.cxl.iam_service.configuration.idpConfig;
import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;

import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.AuthenticationResponse;
import dev.cxl.iam_service.dto.response.IntrospectResponse;
import dev.cxl.iam_service.service.AuthenticationService;
import dev.cxl.iam_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    idpConfig idpConfig;

    @GetMapping("/login")
    public APIResponse<Object> login(@RequestBody AuthenticationRequest authenticationRequest) throws IOException {
        return APIResponse.<Object>builder()
                        .result(idpConfig.getAuthService().login(authenticationRequest))
                .build();

    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticationResponseAPIResponse(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestHeader("authorization") String token, @PathParam("refreshToken")String refreshToken){
        idpConfig.getAuthService().logout(token,refreshToken);
        return APIResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    APIResponse<TokenExchangeResponseUser> logout(@PathParam("refreshToken")String refreshToken) throws Exception {
        return APIResponse.<TokenExchangeResponseUser>builder()
                .result(idpConfig.getAuthService().getRefreshToken(refreshToken))
                .build();
    }

    @PostMapping("/send-email")
    APIResponse<String> sendemail(@RequestParam("email") String email) {
        userService.sendtoken(email);
        return APIResponse.<String>builder().result("Chuc ban thanh cong").build();
    }

    @PutMapping("/change-pass")
    APIResponse<Boolean> changePass(@RequestBody ForgotPassWord forgotPassWord) throws ParseException, JOSEException {

        return APIResponse.<Boolean>builder()
                .result(userService.checkotp(forgotPassWord))
                .build();
    }
}
