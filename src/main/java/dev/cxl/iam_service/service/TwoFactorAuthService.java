package dev.cxl.iam_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.dto.request.AuthenticationRequest;
import dev.cxl.iam_service.dto.request.AuthenticationRequestTwo;
import dev.cxl.iam_service.entity.TwoFactorAuth;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.TwoFactorAuthRepository;
import dev.cxl.iam_service.respository.UserRespository;

@Service
public class TwoFactorAuthService {
    @Autowired
    UserRespository userRespository;

    @Autowired
    EmailService emailService;

    @Autowired
    TwoFactorAuthRepository authRepository;

    public static String generateOtp() {
        Integer otp = (100000 + (int) (Math.random() * 900000));
        return otp.toString();
    }

    public boolean sendOtpMail(AuthenticationRequest authenticationRequest) {
        boolean valid = false;
        User user = userRespository
                .findByUserMail(authenticationRequest.getUserMail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!user.getEnabled()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authentication = passwordEncoder.matches(authenticationRequest.getPassWord(), user.getPassWord());
        if (!authentication) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String otp = TwoFactorAuthService.generateOtp();
        emailService.SendEmail(user.getUserMail(), otp);
        authRepository.save(TwoFactorAuth.builder()
                .userId(user.getUserID())
                .userMail(user.getUserMail())
                .otp(otp)
                .created(LocalDateTime.now())
                .expires(LocalDateTime.now().plusMinutes(5))
                .build());
        valid = true;
        return valid;
    }

    public boolean sendCreatUser(String email) {
        String otp = TwoFactorAuthService.generateOtp();
        StringBuilder message = new StringBuilder();
        message.append("Click vào link thứ nhất để xác nhận đăng kí tài khoàn:<br>");
        message.append("<a href=\"http://localhost:8088/iam/users/confirmCreateUser?email=");
        message.append(email);
        message.append("&otp=");
        message.append(otp);
        message.append("\">Xác nhận tài khoản</a><br>");

        message.append("Và vào link thứ hai để hủy đăng kí:<br>");
        message.append("<a href=\"http://localhost:8088/iam/users/DeleteUser?email=");
        message.append(email);
        message.append("&otp=");
        message.append(otp);
        message.append("\">Hủy đăng ký</a>");

        emailService.SendEmail(email, message.toString());
        authRepository.save(TwoFactorAuth.builder()
                .userMail(email)
                .otp(otp)
                .created(LocalDateTime.now())
                .expires(LocalDateTime.now().plusMinutes(5))
                .build());
        return true;
    }

    public Boolean validateOtp(AuthenticationRequestTwo authenticationRequestTwo, Boolean isCreateUser) {
        Boolean valid = false;
        User user = userRespository
                .findByUserMail(authenticationRequestTwo.getUserMail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        TwoFactorAuth requestTwo = authRepository
                .findFirstByUserMailOrderByCreatedDesc(user.getUserMail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Boolean checkTime = requestTwo.getExpires().isAfter(LocalDateTime.now());
        if (!checkTime) {
            if (!isCreateUser) { // nếu là confirm createuser mà otp hết hạn,xóa user khỏi data base
                userRespository.deleteByUserMail(authenticationRequestTwo.getUserMail());
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            } else {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        }
        Boolean checkOtp = requestTwo.getOtp().equals(authenticationRequestTwo.getOtp());
        if (!(checkOtp && checkTime)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        authRepository.deleteByUserId(user.getUserID());
        valid = true;
        return valid;
    }
}
