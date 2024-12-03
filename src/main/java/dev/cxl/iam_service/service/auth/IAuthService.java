package dev.cxl.iam_service.service.auth;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;

import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.ResetPassword;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;

public interface IAuthService {
    Object login(AuthenticationRequest authenticationRequest);

    boolean logout(String token, String refreshToken) throws ParseException, JOSEException;

    boolean register(UserCreationRequest request);

    TokenExchangeResponseUser getRefreshToken(String refreshToken) throws ParseException, JOSEException;

    Boolean enableUser(String token, String id, UserUpdateRequest request) throws ParseException;

    Boolean resetPassword(String token, String id, ResetPassword resetPassword) throws ParseException;
}
