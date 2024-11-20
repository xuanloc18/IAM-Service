package dev.cxl.iam_service.controller;


import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.AuthenticationResponse;
import dev.cxl.iam_service.dto.response.IntrospectResponse;
import dev.cxl.iam_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

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
    APIResponse<AuthenticationResponse> logout(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        return APIResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();

    }


}
