package dev.cxl.iam_service.service.auth;

import com.nimbusds.jose.JOSEException;
import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.entity.HistoryActivity;
import dev.cxl.iam_service.entity.InvalidateToken;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.enums.UserAction;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.mapper.UserMapper;
import dev.cxl.iam_service.respository.InvalidateTokenRepository;
import dev.cxl.iam_service.respository.RefreshTokenRepository;
import dev.cxl.iam_service.respository.UserRespository;
import dev.cxl.iam_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;

@Service
public class DefaultServiceImpl implements IAuthService{
    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private InvalidateTokenRepository invalidateTokenRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    TwoFactorAuthService twoFactorAuthService;


    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Override
    public Object login(AuthenticationRequest authenticationRequest) {
        twoFactorAuthService.sendOtpMail(authenticationRequest);
        return "OK";
    }

    @Override
    public boolean logout( String accessToken, String refreshToken) {
        try {
            accessToken=accessToken.replace("Bearer ", "");
            var signToken = authenticationService.verifyToken(accessToken);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiry = signToken.getJWTClaimsSet().getExpirationTime();
            String userId = signToken.getJWTClaimsSet().getSubject();
            refreshTokenRepository.deleteByAccessTokenID(jit);

            // Save history activity
            activityService.createHistoryActivity(HistoryActivity.builder()
                    .activityType(UserAction.LOGOUT.name())
                    .activityName(UserAction.LOGOUT.getDescription())
                    .userID(userId)
                    .activityStart(LocalDateTime.now())
                    .browserID(httpServletRequest.getRemoteAddr())
                    .build());

            InvalidateToken invalidateToken =
                    InvalidateToken.builder().id(jit).expiryTime(expiry).build();
            invalidateTokenRepository.save(invalidateToken);
        } catch (AppException exception) {
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean register(UserCreationRequest request) {
        if (userRespository.existsByUserMail(request.getUserMail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
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
    public TokenExchangeResponseUser getRefreshToken(String refreshToken) throws ParseException {
        return authenticationService.refreshToken(refreshToken);
    }
}
