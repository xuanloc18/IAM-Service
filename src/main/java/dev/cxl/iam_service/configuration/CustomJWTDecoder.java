package dev.cxl.iam_service.configuration;

import dev.cxl.iam_service.dto.request.IntrospectRequest;
import dev.cxl.iam_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import dev.cxl.iam_service.service.KeyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJWTDecoder implements JwtDecoder {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private KeyProvider keyProvider;
    private NimbusJwtDecoder nimbusJwtDecoder;
    KeyPair keyPair;
    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            var respone =authenticationService.introspect(IntrospectRequest.builder()
                            .token(token)
                    .build());
            if(!respone.isValid()){
                throw new JwtException("token invalid");
            }
        } catch (ParseException |JOSEException e) {
            throw new JwtException(e.getMessage());

        }
        keyPair=keyProvider.getKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // Phương thức này sẽ trả về public key RSA từ AuthenticationService
            nimbusJwtDecoder=NimbusJwtDecoder.withPublicKey(publicKey)
                    .build();




        return nimbusJwtDecoder.decode(token);
    }
}
