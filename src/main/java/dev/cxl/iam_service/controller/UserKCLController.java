package dev.cxl.iam_service.controller;

import com.nimbusds.jose.JOSEException;
import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.dto.request.LogoutRequest;
import dev.cxl.iam_service.service.UserKCLService;
import jakarta.websocket.server.PathParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/kcl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserKCLController {

    @Autowired
    UserKCLService userKCLService;
    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestHeader("authorization") String token, @PathParam("refreshToken")String refreshToken){
        userKCLService.logOut(token,refreshToken);
        return APIResponse.<Void>builder().build();
    }
}
