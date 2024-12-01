package dev.cxl.iam_service.service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

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

    @NonFinal
    @Autowired
    private InvalidateTokenRepository invalidateTokenRepository;

    @Autowired
    KeyProvider keyProvider;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRespository permissionRespository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
        User user = userRespository
                .findByUserMail(authenticationRequestTwo.getUserMail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Boolean check = twoFactorAuthService.validateOtp(authenticationRequestTwo, false);
        if (!check) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        var token = generrateToken(authenticationRequestTwo.getUserMail());
        SignedJWT signedJWT = SignedJWT.parse(token);
        String id = signedJWT.getJWTClaimsSet().getSubject();
        Date exDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(generrateRefreshToken(authenticationRequestTwo.getUserMail(), id, exDate))
                .authentication(true)
                .build();
    }

    public String generrateToken(String mail) {
        User user =
                (userRespository.findByUserMail(mail)).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
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

    public String generrateRefreshToken(String mail, String accessTokenID, Date date) {
        User user =
                (userRespository.findByUserMail(mail)).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now()
                        .plus(REFESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new RSASSASigner(keyProvider.getKeyPair().getPrivate()));
            refreshTokenRepository.save(RefreshToken.builder()
                    .refreshToken(jwsObject.serialize())
                    .expiresAccessToken(date)
                    .accessTokenID(accessTokenID)
                    .build());
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("can not create access token", e);
            throw new RuntimeException(e);
        }
    }

    //    private String buildScrope(User user) {
    //        StringJoiner stringJoiner = new StringJoiner(" "); // các phần tử cách nhau bới " "
    //        if (!CollectionUtils.isEmpty(user.getRoles())) {
    //            List<Role> roles = roleRepository.findAllById(user.getRoles());
    //            roles.forEach(role -> {
    //                stringJoiner.add(role.getName());
    //                if (!CollectionUtils.isEmpty(role.getPermissions())) {
    //                    List<Permission> permissions = permissionRespository.findAllById(role.getPermissions());
    //                    permissions.forEach(permission -> stringJoiner.add(permission.getName()));
    //                }
    //            });
    //        }
    //        return stringJoiner.toString();
    //    }

    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        RSASSAVerifier rsassaVerifier =
                new RSASSAVerifier((RSAPublicKey) keyProvider.getKeyPair().getPublic());
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean veri = signedJWT.verify(rsassaVerifier);
        if (!(veri && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    public void logout(String accessToken, String refreshToken) throws ParseException, JOSEException {
        try {
            accessToken = accessToken.replace("Bearer ", "");
            var signToken = verifyToken(accessToken);
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
    }

    public TokenExchangeResponseUser refreshToken(String refreshTokenn) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(refreshTokenn);
        RefreshToken refreshToken = refreshTokenRepository
                .findRefreshTokenByRefreshToken(refreshTokenn)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        Boolean checkTime =
                signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().isAfter(Instant.now());
        if (!checkTime) throw new AppException(ErrorCode.UNAUTHENTICATED);
        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(refreshToken.getAccessTokenID())
                .expiryTime(refreshToken.getExpiresAccessToken())
                .build();
        invalidateTokenRepository.save(invalidateToken);
        refreshTokenRepository.deleteByAccessTokenID(invalidateToken.getId());
        var userID = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRespository.findById(userID).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        var token = generrateToken(user.getUserMail());
        SignedJWT signedJWT1 = SignedJWT.parse(token);
        var refeshToken = generrateRefreshToken(
                user.getUserMail(),
                signedJWT1.getJWTClaimsSet().getJWTID(),
                signedJWT1.getJWTClaimsSet().getExpirationTime());
        return TokenExchangeResponseUser.builder()
                .accessToken(token)
                .refreshToken(refeshToken)
                .build();
    }
}
