package dev.cxl.iam_service.configuration;

import dev.cxl.iam_service.dto.request.IntrospectRequest;
import dev.cxl.iam_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJWTDecoder implements JwtDecoder {
    @Autowired
    private AuthenticationService authenticationService;
    private NimbusJwtDecoder nimbusJwtDecoder=null;
    @Value("${jwt.signerKey}")
    private  String SINGER_KEY;
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
        if (Objects.isNull(nimbusJwtDecoder)){//check xem nếu nimbus đã được tao thì chỉ mang ra duùng
            SecretKeySpec secretKeySpec=new SecretKeySpec(SINGER_KEY.getBytes(),"HS512");
            nimbusJwtDecoder=NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }



        return nimbusJwtDecoder.decode(token);
    }
}
