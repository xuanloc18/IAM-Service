package dev.cxl.iam_service.service.auth;

import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.UserCreationRequest;

import java.text.ParseException;

public interface IAuthService {
    Object login(AuthenticationRequest authenticationRequest);
    boolean logout(String token, String refreshToken);
    boolean register(UserCreationRequest request);
    TokenExchangeResponseUser getRefreshToken(String refreshToken) throws ParseException;
}
