package dev.cxl.iam_service.logging;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)  // Đảm bảo filter này chạy sau các filter khác
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper để xử lý JSON

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // Đảm bảo request được wrap lại để có thể đọc body nhiều lần
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        // Chạy tiếp các filter khác
        filterChain.doFilter(cachingRequestWrapper, servletResponse);

        // Lấy body từ request đã được wrap
        String requestBody = new String(cachingRequestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        // Log các tham số của request (trừ password)
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, String> sanitizedParams = parameterMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase("password"))  // Loại bỏ 'password'
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.toString(e.getValue())));

        // Chỉnh sửa body để ẩn password
        if (!requestBody.isEmpty()) {
            try {
                JsonNode rootNode = objectMapper.readTree(requestBody);
                if (rootNode.has("passWord")) {
                    ((ObjectNode) rootNode).put("passWord", "[******]");
                }
                requestBody = rootNode.toString(); // Cập nhật lại request body sau khi thay thế
            } catch (Exception e) {
                logger.error("Error processing request body", e);
            }
        }

        // Kiểm tra mã trạng thái response và log nếu là 500
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        int status = httpServletResponse.getStatus();

        // Nếu lỗi 500, log cả request và response
        if (status == 500) {
            logger.error("Error occurred - Response Status: 500, Request - Method: {} URI: {}, Params: {}, Body: {}",
                    httpServletRequest.getMethod(),
                    httpServletRequest.getRequestURI(),
                    sanitizedParams,
                    requestBody);

            // Log thông tin của response (nếu có thêm thông tin muốn log từ response)
            logger.error("Response - Status: {}", status);
        }
    }
}
