package dev.cxl.iam_service.service;

import org.springframework.data.domain.Pageable; // Đúng

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import dev.cxl.iam_service.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.entity.HistoryActivity;
import dev.cxl.iam_service.entity.InvalidateToken;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.enums.UserAction;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.mapper.UserMapper;
import dev.cxl.iam_service.respository.InvalidateTokenRepository;
import dev.cxl.iam_service.respository.UserRespository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

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
    UserKCLService userKCLService;

    @Value("${idp.enable}")
    Boolean idpEnable;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRespository.existsByUserMail(request.getUserMail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setUserKCLID(userKCLService.createUserKCL(request));
        user.setEnabled(false);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
//        HashSet<String> roles = new HashSet<>();
//        if (CollectionUtils.isEmpty(user.getRoles())) roles.add("USER");
//        user.setRoles(roles);
        twoFactorAuthService.sendCreatUser(user.getUserMail());
        return userMapper.toUserResponse(userRespository.save(user));
    }

    public UserResponse confirmCreateUser(String email, String otp) {
        User user =
                userRespository.findByUserMail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Boolean check = twoFactorAuthService.validateOtp(
                AuthenticationRequestTwo.builder().userMail(email).otp(otp).build(), true);
        if (!check) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        user.setEnabled(true);

        // Save history activity
        activityService.createHistoryActivity(HistoryActivity.builder()
                .activityType(UserAction.CREATE.name())
                .activityName(UserAction.CREATE.getDescription())
                .userID(user.getUserID())
                .activityStart(LocalDateTime.now())
                .browserID(httpServletRequest.getRemoteAddr())
                .build());

        return userMapper.toUserResponse(userRespository.save(user));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Sort sort=Sort.by( "userID").descending();
        Pageable pageable= PageRequest.of(page-1,size,sort);
        var pageData=userRespository.findAll(pageable);
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(user -> userMapper.toUserResponse(user)).toList())
                .build();
    }

    public UserResponse updareUser(UserUpdateRequest request) {
        String userID = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRespository.findById(userID).orElseThrow(() -> new RuntimeException("user not found"));
        user = userMapper.updateUser(user, request);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));

        // Save history activity
        activityService.createHistoryActivity(HistoryActivity.builder()
                .activityType(UserAction.UPDATE_PROFILE.name())
                .activityName(UserAction.UPDATE_PROFILE.getDescription())
                .userID(user.getUserID())
                .activityStart(LocalDateTime.now())
                .browserID(httpServletRequest.getRemoteAddr())
                .build());
        return userMapper.toUserResponse(userRespository.save(user));
    }

    public UserResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user ;
        if(idpEnable){
            user = userRespository.findByUserKCLID(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else {
            user = userRespository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }

        return userMapper.toUserResponse(user);
    }
    public UserResponse getInfor(String id) {
        User user;
        if(idpEnable){
            user = userRespository.findByUserKCLID(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        else {
            user = userRespository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }

        return userMapper.toUserResponse(user);
    }

    public Boolean replacePassword(UserRepalcePass userRepalcePass) {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRespository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info(user.getUserMail());
        log.info(user.getPassWord());
        Boolean checkPass = passwordEncoder.matches(userRepalcePass.getOldPassword(), user.getPassWord());
        if (!checkPass) throw new AppException(ErrorCode.INVALID_KEY);
        Boolean aBoolean = userRepalcePass.getConfirmPassword().equals(userRepalcePass.getNewPassword());
        if (!aBoolean) throw new RuntimeException("password does not confirm");

        user.setPassWord(passwordEncoder.encode(userRepalcePass.getNewPassword()));
        log.info(user.getPassWord());

        // Save history activity
        activityService.createHistoryActivity(HistoryActivity.builder()
                .activityType(UserAction.CHANGE_PASSWORD.name())
                .activityName(UserAction.CHANGE_PASSWORD.getDescription())
                .userID(user.getUserID())
                .activityStart(LocalDateTime.now())
                .browserID(httpServletRequest.getRemoteAddr())
                .build());
        userRespository.save(user);
        return true;
    }

    public String sendtoken(String email) {
        userRespository.findByUserMail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = authenticationService.generrateToken(email);
        emailService.SendEmail(email, token);
        return "Chúc bạn thành công";
    }

    public Boolean checkotp(ForgotPassWord forgotPassWord) throws ParseException, JOSEException {
        var respone = authenticationService.introspect(
                IntrospectRequest.builder().token(forgotPassWord.getToken()).build());
        if (!respone.isValid()) {
            return false;
        }
        SignedJWT signedJWT = SignedJWT.parse(forgotPassWord.getToken());
        String userid = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRespository.findById(userid).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setPassWord(passwordEncoder.encode(forgotPassWord.getNewPass()));
        userRespository.save(user);

        // Save history activity
        invalidateTokenRepository.save(InvalidateToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build());
        return true;
    }

    public void del(String email, String otp) {
        User user =
                userRespository.findByUserMail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user.getEnabled()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        userRespository.delete(user);
    }
}
