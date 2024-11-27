package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.dto.identity.Logout;
import dev.cxl.iam_service.dto.request.LogoutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import dev.cxl.iam_service.dto.identity.TokenExchangeParam;
import dev.cxl.iam_service.dto.identity.TokenExchangeResponse;
import dev.cxl.iam_service.dto.identity.UserCreationParam;
import feign.QueryMap;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IndentityClient {
    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/CXL/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParam param);

    @PostMapping(
            value = "/realms/CXL/protocol/openid-connect/logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<?> logoutUser(@RequestHeader("authorization") String token,
                                 @QueryMap Logout logout);
}