package com.islevilla.ching.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ChatRedisConfig {

    //Redis 連線 使用db01
    @Bean(name = "redisConnectionFactoryDb1")
    public RedisConnectionFactory redisConnectionFactoryDb1() {
    	LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", 6379);
        factory.setDatabase(1);
        return factory;
    }

    // edis String Template (用於簡單的 Key-Value) 
    @Bean(name = "redisStringTemplatedDb1")
    public RedisTemplate<String, String> redisStringTemplate(
            RedisConnectionFactory redisConnectionFactoryDb1) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryDb1);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // Redis JSON Template (物件用) 
    @Bean(name = "redisJsonTemplatedDb1")
    public RedisTemplate<String, Object> redisJsonTemplate(
            RedisConnectionFactory redisConnectionFactoryDb1) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryDb1);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
