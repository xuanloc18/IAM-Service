package dev.cxl.iam_service.configuration;

import org.springframework.context.annotation.Bean;

public class RedisConfig {
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());   // Key dạng String
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // Value dạng String
        return redisTemplate;
    }
}
