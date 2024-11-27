package dev.cxl.iam_service.service;

import java.text.ParseException;
import java.util.List;

import com.nimbusds.jwt.SignedJWT;
import dev.cxl.iam_service.dto.identity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.respository.IndentityClient;
import lombok.experimental.NonFinal;

@Service
public class UserKCLService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    IndentityClient indentityClient;

    @Value("${idp.client-id}")
    @NonFinal
    String idpClientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String idpClientSecret;

    public String createUserKCL(UserCreationRequest request) {

        var creationResponse = indentityClient.createUser(
                "Bearer " + tokenExchangeResponse().getAccessToken(),
                UserCreationParam.builder()
                        .username(request.getFirstName())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getUserMail())
                        .enabled(true)
                        .emailVerified(true)
                        .credentials(List.of(Credential.builder()
                                .type("password")
                                .temporary(false)
                                .value(request.getPassWord())
                                .build()))
                        .build());
        String userKCLID = extractUserId(creationResponse);
        log.info("userKCLID {}", userKCLID);
        return userKCLID;
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] parts = location.split("/");
        return parts[parts.length - 1];
    }

    public TokenExchangeResponse tokenExchangeResponse() {
        return indentityClient.exchangToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(idpClientId)
                .client_secret(idpClientSecret)
                .scope("openid")
                .build());
    }

    public  void logOut(String token,String refreshToken)  {
      token="Bearer"+ token;
        Logout logout=Logout.builder()
                .refresh_token(refreshToken)
                .client_id(idpClientId)
                .client_secret(idpClientSecret)
                .build();
        indentityClient.logoutUser(token,logout);
    }
}