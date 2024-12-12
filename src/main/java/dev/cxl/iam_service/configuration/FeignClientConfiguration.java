package dev.cxl.iam_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.cxl.iam_service.service.auth.FeignClientInterceptor;
import feign.RequestInterceptor;

@Configuration
public class FeignClientConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor(FeignClientInterceptor feignClientInterceptor) {
        return feignClientInterceptor;
    }
}
