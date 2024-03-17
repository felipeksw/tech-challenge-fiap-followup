package com.fiap.techchallenge.followup.gateway.port.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fiap.techchallenge.followup.gateway.entity.OrderEntity;
import com.fiap.techchallenge.followup.gateway.entity.OrderItemsEntity;

@ExtendWith(SpringExtension.class)
class RedisCacheTest {

    @InjectMocks
    private RedisCache redisCache;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private RedisServerCommands redisServerCommands;

    private final OrderItemsEntity orderItemsEntityMock = getOrderItemsEntityMock();
    private final OrderEntity orderEntityMock = getOrderEntityMock();

    @BeforeEach
    void setUp() {
        // Configuração do mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.serverCommands()).thenReturn(redisServerCommands);

    }

    @Test
    void when_ClearAllCache_Then_FlushAllRedis() {
        redisCache.clearAllCaches();
        verify(redisServerCommands, only()).flushAll();
    }

    @Test
    void when_ClearAllCacheAndConnectionFactoryIsNull_Then_ThrowAException() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        NullPointerException expectedException = assertThrowsExactly(NullPointerException.class,
                () -> redisCache.clearAllCaches());

        assertEquals("The redis connection factory mustn't  null", expectedException.getMessage());
        verify(redisServerCommands, never()).flushAll();
    }

    @Test
    void when_GetAllValueByKeys_Then_GetMultiValuesFromRedis() {
        Set<String> setOfKeys = Set.of("orderStatus::1");
        redisCache.getAllDataByKeys(setOfKeys);
        verify(valueOperations).multiGet(setOfKeys);
    }

    @Test
    void when_GetAllKeysByNamePatter_Then_GetRedisKeys() {
        redisCache.getAllKeysByNamePattern("orderStatus::");
        verify(redisTemplate).keys("orderStatus::");
    }

    @Test
    void when_SetKeyWithExpirationTimeInMinutes_Then_SetOnRedisWithExpiration() {
        redisCache.setKeyWithExpirationTimeInMinutes("orderStatus::1", orderEntityMock, 3);
        verify(valueOperations, only()).set("orderStatus::1", orderEntityMock, 3l,
                TimeUnit.MINUTES);
    }

    @Test
    void when_SetKeyWithoutExpirationTimeInMinutes_Then_SetOnRedisWithoutExpiration() {
        redisCache.setKeyWithoutExpirationTime("orderStatus::1", orderItemsEntityMock);
        verify(valueOperations, only()).set("orderStatus::1", orderItemsEntityMock);
    }

    @Test
    void testSetMultiKeyWithoutExpirationTime() {
        Map<String, Object> keysValuesToSet = new HashMap<String, Object>();
        keysValuesToSet.put("orderStatus::1", orderEntityMock);
        keysValuesToSet.put("orderStatus::2", orderItemsEntityMock);

        redisCache.setMultiKeyWithoutExpirationTime(keysValuesToSet);
        verify(valueOperations, only()).multiSet(keysValuesToSet);
    }

    private OrderEntity getOrderEntityMock() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1l);
        orderEntity.setPrice(BigDecimal.valueOf(10l));
        orderEntity.setClient("Felipe");
        orderEntity.setStatus("recebido");
        orderEntity.setPaymentMethod("Cartao");
        orderEntity.setOrderItems(List.of());
        orderEntity.setCustomerId(1l);
        orderEntity.setCreatedAt(LocalDateTime.now());

        return orderEntity;

    }

    private OrderItemsEntity getOrderItemsEntityMock() {
        OrderItemsEntity orderItemsEntity = new OrderItemsEntity();
        orderItemsEntity.setId(null);
        orderItemsEntity.setDescription(null);
        orderItemsEntity.setOrders(getOrderEntityMock());
        orderItemsEntity.setProductId(null);
        orderItemsEntity.setQuantity(null);

        return orderItemsEntity;
    }
}
