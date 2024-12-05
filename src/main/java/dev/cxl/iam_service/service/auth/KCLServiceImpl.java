package dev.cxl.iam_service.service.auth;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.ResetPassword;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.UserRespository;
import dev.cxl.iam_service.service.*;

@Service
public class KCLServiceImpl implements IAuthService {
    @Autowired
    UserService userService;

    @Autowired
    UserKCLService userKCLService;

    @Autowired
    private UserRespository userRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UtilUserService utilUser;

    @Override
    public Object login(AuthenticationRequest authenticationRequest) {
        return userKCLService.tokenExchangeResponseUser(authenticationRequest);
    }

    @Override
    public boolean logout(String token, String refreshToken) {
        userKCLService.logOut(token, refreshToken);
        return true;
    }

    @Override
    public boolean register(UserCreationRequest request) {
        userService.createUser(request);
        return true;
    }

    @Override
    public TokenExchangeResponseUser getRefreshToken(String refreshToken) {
        return userKCLService.refreshToken(refreshToken);
    }

    @Override
    public Boolean enableUser(String token, String id, UserUpdateRequest request) throws ParseException {
        User user = userRespository.findByUserKCLID(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEnabled(request.getEnabled());
        userRespository.save(user);
        userKCLService.enableUser(userKCLService.tokenExchangeResponse().getAccessToken(), id, request);
        return null;
    }

    @Override
    public Boolean resetPassword(String token, String id, ResetPassword resetPassword) throws ParseException {
        User user = utilUser.finUserId(id);
        userKCLService.resetPassWord(
                userKCLService.tokenExchangeResponse().getAccessToken(), user.getUserKCLID(), resetPassword);
        user.setPassWord(passwordEncoder.encode(resetPassword.getValue()));
        userRespository.save(user);
        return null;
    }
}
