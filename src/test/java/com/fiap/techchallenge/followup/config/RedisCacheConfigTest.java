package com.fiap.techchallenge.followup.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RedisCacheConfigTest {

    @InjectMocks
    private RedisCacheConfig redisCacheConfig;

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Test
    void when_CreateACacheConfiguration_Then_UseTheSetConfiguration() {
        RedisCacheConfiguration createdCacheConfiguration = redisCacheConfig.cacheConfiguration();

        assertAll("Check all configuration of cache",
                () -> assertFalse(createdCacheConfiguration.getAllowCacheNullValues()));

    }

    @Test
    void when_CreateARedisTemplate_Then_UseTheSetConfiguration() {
        RedisTemplate<String, Object> createdRedisTemplate = redisCacheConfig.redisTemplate(connectionFactory);

        assertAll("Check all configuration of redis template",
                () -> assertEquals(connectionFactory, createdRedisTemplate.getConnectionFactory()),
                () -> assertEquals(StringRedisSerializer.class, createdRedisTemplate.getKeySerializer().getClass()),
                () -> assertEquals(GenericJackson2JsonRedisSerializer.class,
                        createdRedisTemplate.getValueSerializer().getClass()),
                () -> assertEquals(StringRedisSerializer.class, createdRedisTemplate.getHashKeySerializer().getClass()),
                () -> assertEquals(GenericToStringSerializer.class,
                        createdRedisTemplate.getHashValueSerializer().getClass()));
    }
}
