package com.fiap.techchallenge.followup.application.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.gateway.port.CachePort;
import com.fiap.techchallenge.followup.gateway.repository.OrderRepository;

@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CachePort cachePort;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testFindAllWithActiveStatus() {
        Order orderMock = new Order(1l, "recebido", LocalDate.now());
        when(cachePort.getAllKeysByNamePattern(any())).thenReturn(Set.of("1"));
        List<Object> orderMockList = List.of(orderMock);
        when(cachePort.getAllDataByKeys(any())).thenReturn(orderMockList);

        List<Order> listOrder = orderService.findAllWithActiveStatus();

        assertEquals(orderMock, listOrder.get(0));
    }
}
