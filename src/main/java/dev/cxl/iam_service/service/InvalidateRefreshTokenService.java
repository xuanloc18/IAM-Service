package dev.cxl.iam_service.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.SignedJWT;

import dev.cxl.iam_service.entity.InvalidateRefreshToken;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.InvalidRefreshTokenRepository;

@Service
public class InvalidateRefreshTokenService {
    @Autowired
    InvalidRefreshTokenRepository invalidRefreshTokenRepository;

    public void invalid(String id) {
        invalidRefreshTokenRepository.save(
                InvalidateRefreshToken.builder().id(id).build());
    }

    public void checkExits(SignedJWT signedJWT) throws ParseException {
        Boolean check = invalidRefreshTokenRepository.existsById(
                signedJWT.getJWTClaimsSet().getJWTID());
        if (check) throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
}
