package dev.cxl.iam_service.service.auth;

import org.springframework.stereotype.Component;

import dev.cxl.iam_service.service.UserKCLService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {
    private final UserKCLService userKCLService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
        log.info(token);
        requestTemplate.header("Authorization", token);
    }
}
