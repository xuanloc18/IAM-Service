package dev.cxl.iam_service.service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import dev.cxl.iam_service.configuration.KeyProvider;
import dev.cxl.iam_service.dto.AuthenticationProperties;
import dev.cxl.iam_service.dto.identity.TokenExchangeResponseUser;
import dev.cxl.iam_service.dto.request.*;
import dev.cxl.iam_service.dto.response.AuthenticationResponse;
import dev.cxl.iam_service.dto.response.IntrospectResponse;
import dev.cxl.iam_service.entity.*;
import dev.cxl.iam_service.enums.UserAction;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableConfigurationProperties(AuthenticationProperties.class)
@RequiredArgsConstructor // là một annotation trong Lombok tự động tạo constructor cho các trường (fields) có giá trị là
// final hoặc được đánh dấu là @NonNull
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    UserRespository userRespository;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFESHABLE_DURATION;

    @Autowired
    KeyProvider keyProvider;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private InvalidateRefreshTokenService invalidateRefreshTokenService;

    @Autowired
    private InvalidateTokenService invalidateTokenService;

    @Autowired
    private UtilUserService userUtil;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean valid = true;
        try {
            verifyToken(token);
        } catch (AppException appException) {
            valid = false;
        }
        return IntrospectResponse.builder().valid(valid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequestTwo authenticationRequestTwo)
            throws ParseException {
        User user = userUtil.finUserMail(authenticationRequestTwo.getUserMail());
        Boolean check = twoFactorAuthService.validateOtp(authenticationRequestTwo, false);
        if (!check) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        // activity
        activityService.createHistoryActivity(user.getUserID(), UserAction.LOGIN);
        var token = generrateToken(authenticationRequestTwo.getUserMail());
        SignedJWT signedJWT = SignedJWT.parse(token);
        String idToken = signedJWT.getJWTClaimsSet().getJWTID();
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(generrateRefreshToken(user.getUserID(), idToken))
                .authentication(true)
                .build();
    }

    public String generrateToken(String mail) {
        User user = userUtil.finUserMail(mail);
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserID().toString())
                .issuer("cxl")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("name", user.getUserName())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new RSASSASigner(keyProvider.getKeyPair().getPrivate()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("can not create token", e);
            throw new RuntimeException(e);
        }
    }

    public String generrateRefreshToken(String userId, String idToken) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now()
                        .plus(REFESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("idToken", idToken)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new RSASSASigner(keyProvider.getKeyPair().getPrivate()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("can not create access token", e);
            throw new RuntimeException(e);
        }
    }

    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        RSASSAVerifier rsassaVerifier =
                new RSASSAVerifier((RSAPublicKey) keyProvider.getKeyPair().getPublic());
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean veri = signedJWT.verify(rsassaVerifier);
        if (!(veri && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        invalidateTokenService.checkExits(signedJWT.getJWTClaimsSet().getJWTID());
        return signedJWT;
    }

    public void logout(String accessToken, String refreshToken) throws ParseException, JOSEException {
        try {
            accessToken = accessToken.replace("Bearer ", "");
            var signToken = verifyToken(accessToken);
            var signRefreshToken = verifyToken(refreshToken);
            String tokenID = signToken.getJWTClaimsSet().getJWTID();
            String refreshTokenID = signRefreshToken.getJWTClaimsSet().getJWTID();
            String userID = signToken.getJWTClaimsSet().getSubject();
            invalidateTokenService.invalid(tokenID);
            invalidateRefreshTokenService.invalid(refreshTokenID);

            // activity
            activityService.createHistoryActivity(userID, UserAction.LOGOUT);
        } catch (AppException exception) {
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public TokenExchangeResponseUser refreshToken(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        invalidateRefreshTokenService.checkExits(signedJWT);
        verifyToken(refreshToken);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String tokenId = signedJWT.getJWTClaimsSet().getStringClaim("idToken");
        invalidateTokenService.invalid(tokenId);
        invalidateRefreshTokenService.invalid(signedJWT.getJWTClaimsSet().getJWTID());
        User user = userRespository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        var token = generrateToken(user.getUserMail());
        SignedJWT signedJWT1 = SignedJWT.parse(token);
        return TokenExchangeResponseUser.builder()
                .accessToken(token)
                .refreshToken(generrateRefreshToken(
                        userId, signedJWT1.getJWTClaimsSet().getJWTID()))
                .build();
    }
}
