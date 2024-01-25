package com.fiap.techchallenge.followup.gateway.port.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fiap.techchallenge.followup.gateway.port.CachePort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCache implements CachePort {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void clearAllCaches() {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();

        Objects.requireNonNull(redisConnectionFactory, "The redis connection factory mustn't  null");

        RedisConnection redisConnection = redisConnectionFactory.getConnection();

        redisConnection.serverCommands().flushAll();
    }

    @Override
    public Set<String> getAllKeysByNamePattern(String keyNamePattern) {
        return redisTemplate.keys(keyNamePattern);
    }

    @Override
    public List<Object> getAllDataByKeys(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public void setKeyWithExpirationTimeInMinutes(String key, Object value, Integer timeExpiration) {
        redisTemplate.opsForValue().set(key, value, timeExpiration,
                TimeUnit.MINUTES);
    }

    @Override
    public void setKeyWithoutExpirationTime(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setMultiKeyWithoutExpirationTime(Map<? extends String, ? extends Object> keyValueList) {
        redisTemplate.opsForValue().multiSet(keyValueList);
    }

}
