package dev.cxl.iam_service.configuration;

import dev.cxl.iam_service.dto.request.APIResponse;
import dev.cxl.iam_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
//bắt exception 401 bên tầng filter
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode=ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);//cho biết dữ liệu sẽ là kiểu json
        APIResponse<?> apiResponse=APIResponse.builder()
                .code(errorCode.getCode())
                .mesage(errorCode.getMessage())
                .build();
        ObjectMapper objectMapper=new ObjectMapper();//đổi object về json
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
