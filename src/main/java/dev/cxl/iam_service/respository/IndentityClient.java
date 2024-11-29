package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.dto.identity.*;
import dev.cxl.iam_service.dto.request.LogoutRequest;
import dev.cxl.iam_service.dto.request.ResetPassword;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import feign.QueryMap;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IndentityClient {
    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangToken(@QueryMap TokenExchangeParam param);

    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseUser exchangTokenUser(@QueryMap TokenExchangeParamUser param);

    @PostMapping(value = "/admin/realms/CXL/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);

    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<?> logoutUser(@RequestHeader("authorization") String token,
                                 @QueryMap Logout logout);
    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseUser refrehToken(@QueryMap TokenExchangeRefresh refresh);
    @PutMapping(
            value = "/admin/realms/CXL/users/{userID}",consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> enableUser(@RequestHeader("authorization")String token, @PathVariable("userID")String userId, @RequestBody UserUpdateRequest update);

    @PutMapping(
            value = "/admin/realms/CXL/users/{userID}/reset-password",consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> resetPassWord(@RequestHeader("authorization")String token, @PathVariable("userID")String userId, @RequestBody ResetPassword resetPassword);


}