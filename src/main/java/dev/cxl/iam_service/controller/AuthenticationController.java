package dev.cxl.iam_service.controller;

import java.io.IOException;
import java.text.ParseException;

import jakarta.servlet.http.HttpServletResponse;

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

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String keycloakLoginUrl =
                "http://localhost:8080/realms/CXL/protocol/openid-connect/auth?client_id=security-admin" +
                        "-console&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fadmin%2FCXL%2Fconsole%2F" +
                        "&state=235933d8-3e44-45df-bc88-7746d12e9cee&response_mode=query&response_type=" +
                        "code&scope=openid&nonce=17885cb2-95ef-40cd-97f0-640bf04a4651&code_challenge=e0" +
                        "5dcA9mJ1ABqUYz7N3EmYwrIOQ-N5RMlpyCBJe_SzQ&code_challenge_method=S256";

        response.sendRedirect(keycloakLoginUrl); // Chuyển hướng trình duyệt tới URL Keycloak
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticationResponseAPIResponse(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return APIResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> logout(@RequestBody RefreshRequest request) throws Exception {
        return APIResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
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
