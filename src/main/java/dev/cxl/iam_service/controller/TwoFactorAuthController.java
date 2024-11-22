package dev.cxl.iam_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.AuthenticationRequestTwo;
import dev.cxl.iam_service.dto.response.AuthenticationResponse;
import dev.cxl.iam_service.service.AuthenticationService;
import dev.cxl.iam_service.service.TwoFactorAuthService;

@RestController
@RequestMapping("/auth")
public class TwoFactorAuthController {
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/tfa-first")
    APIResponse<Boolean> tfaFirst(@RequestBody AuthenticationRequest authenticationRequest) {
        return APIResponse.<Boolean>builder()
                .result(twoFactorAuthService.sendOtpMail(authenticationRequest))
                .build();
    }
    //    @PostMapping("/tfa-two")
    //    APIResponse<Boolean> tfaTwo(@RequestBody AuthenticationRequestTwo two) {
    //        return APIResponse.<Boolean>builder()
    //                .result(twoFactorAuthService.validateOtp(two))
    //                .build();
    //
    //    }
    @PostMapping("/tfa-two")
    APIResponse<AuthenticationResponse> authenticationResponseAPIResponse(@RequestBody AuthenticationRequestTwo two) {
        var result = authenticationService.authenticate(two);
        return APIResponse.<AuthenticationResponse>builder().result(result).build();
    }
}
