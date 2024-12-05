package dev.cxl.iam_service.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.entity.InvalidateToken;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.InvalidateTokenRepository;

@Service
public class InvalidateTokenService {
    @Autowired
    InvalidateTokenRepository tokenRepository;

    public void invalid(String id) {
        tokenRepository.save(InvalidateToken.builder().id(id).build());
    }

    public void checkExits(String id) throws ParseException {
        Boolean check = tokenRepository.existsById(id);
        if (check) throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
}
