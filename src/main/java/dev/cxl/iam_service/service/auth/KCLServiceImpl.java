package dev.cxl.iam_service.service.auth;

import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.mapper.UserMapper;
import dev.cxl.iam_service.respository.InvalidateTokenRepository;
import dev.cxl.iam_service.respository.UserRespository;
import dev.cxl.iam_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;

@Service
public class KCLServiceImpl implements IAuthService{
    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    TwoFactorAuthService twoFactorAuthService;

    @Autowired
    UserKCLService userKCLService;



    @Override
    public Object login(AuthenticationRequest authenticationRequest) {
      return   userKCLService.tokenExchangeResponseUser(authenticationRequest);

    }

    @Override
    public boolean logout(String token, String refreshToken) {
        userKCLService.logOut(token, refreshToken);
        return true;
    }

    @Override
    public boolean register(UserCreationRequest request) {
        if (userRespository.existsByUserMail(request.getUserMail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setUserKCLID(userKCLService.createUserKCL(request));
        user.setEnabled(false);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        HashSet<String> roles = new HashSet<>();
        if (CollectionUtils.isEmpty(user.getRoles())) roles.add("USER");
        user.setRoles(roles);
        twoFactorAuthService.sendCreatUser(user.getUserMail());
        userMapper.toUserResponse(userRespository.save(user));
        return true;
    }

    @Override
    public TokenExchangeResponseUser getRefreshToken(String refreshToken) {
         return userKCLService.refreshToken(refreshToken);

    }
}
