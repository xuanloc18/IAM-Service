package dev.cxl.iam_service.configuration;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule localDateTimeModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new CustomLocalDateDeserializer());
        return simpleModule;
    }
}
