package com.islevilla.chen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 設定類
 * @Configuration 告訴 Spring 這是一個設定檔。
 *
 */
@Configuration
public class RedisConfig {

    /**
     * 自定義 RedisTemplate Bean，用於設定資料在 Redis 中的序列化方式。
     * @param connectionFactory Spring Boot 自動提供的 Redis 連線工廠
     * @return 設定好的 RedisTemplate 實例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // --- 設定序列化器，確保資料的可讀性 ---

        // Key 使用 String 序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 使用 JSON 序列化器 (GenericJackson2JsonRedisSerializer)，可以處理各種 Java 物件
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}