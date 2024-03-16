package com.fiap.techchallenge.followup.application.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.gateway.entity.OrderEntity;
import com.fiap.techchallenge.followup.gateway.entity.OrderItemsEntity;
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
    void when_FindAllOrderStatusActive_Then_ReturnListOfAllStatus() {
        List<Object> orderMockList = List.of(new Order(1l, "order_received", LocalDate.now()),
                new Order(2l, "order_in_production", LocalDate.now()),
                new Order(3l, "order_completed", LocalDate.now()),
                new Order(4l, "order_delivered", LocalDate.now()));
        when(cachePort.getAllKeysByNamePattern(any())).thenReturn(Set.of("1"));
        when(cachePort.getAllDataByKeys(any())).thenReturn(orderMockList);

        List<Order> listOrder = orderService.findAllOrderStatusWithActiveStatus();

        assertEquals(orderMockList, listOrder);
    }

    @Test
    void when_UpdateStatusOfAOrderDoesntExist_Then_ThrowAException() {

        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException receivedException = assertThrowsExactly(NotFoundException.class,
                () -> orderService.updateStatus(1l, new Status("order_delivered")));

        assertEquals("Ordem não encontrada", receivedException.getMessage());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(cachePort);

    }

    @Test
    void when_UpdateStatusOfAOrderAndNextStatusIsInvalid_Then_ThrowAException() {
        OrderEntity orderEntityMock = OrderEntity.builder()
                .id(1l)
                .status("order_received")
                .createdAt(LocalDate.now())
                .build();

        when(orderRepository.findById(1l)).thenReturn(Optional.of(orderEntityMock));

        InvalidDataException receivedException = assertThrowsExactly(InvalidDataException.class,
                () -> orderService.updateStatus(1l, new Status("order_completed")));

        assertEquals("Status order_received cannot be changed to order_completed", receivedException.getMessage());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(cachePort);

    }

    @Test
    void when_UpdateStatusOfAOrderToFinalized_Then_SetOnCacheWith3MinExpirationTime() {
        OrderEntity orderEntityMock = OrderEntity.builder()
                .id(1l)
                .status("order_completed")
                .createdAt(LocalDate.of(2000, 1, 1))
                .build();

        when(orderRepository.findById(1l)).thenReturn(Optional.of(orderEntityMock));

        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<OrderEntity> orderEntitycaptor = ArgumentCaptor.forClass(OrderEntity.class);

        Order receivedOrder = orderService.updateStatus(1l, new Status("order_delivered"));

        Order expectedOrder = Order.builder()
                .id(1l)
                .status("order_delivered")
                .createdAt(LocalDate.of(2000, 1, 1))
                .build();

        assertEquals(expectedOrder, receivedOrder);
        verify(orderRepository).save(orderEntitycaptor.capture());
        assertEquals("order_delivered", orderEntitycaptor.getValue().getStatus());
        verify(cachePort, only()).setKeyWithExpirationTimeInMinutes("orderStatus::1",
                orderEntitycaptor.getValue().toDomain(), 3);
    }

    @Test
    void when_UpdateStatusOfAOrderToActiveStatus_Then_SetOnCacheWithoutExpirationTime() {
        OrderEntity orderEntityMock = OrderEntity.builder()
                .id(1l)
                .status("order_received")
                .createdAt(LocalDate.of(2000, 1, 1))
                .client("Felipe")
                .customerId(1l)
                .paymentMethod("Cartao")
                .price(BigDecimal.TEN)
                .orderItems(List.of(OrderItemsEntity.builder()
                        .id(1l)
                        .description("Descricao")
                        .productId(1l)
                        .quantity(10l)
                        .orders(new OrderEntity())
                        .build()))
                .build();

        when(orderRepository.findById(1l)).thenReturn(Optional.of(orderEntityMock));

        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<OrderEntity> orderEntitycaptor = ArgumentCaptor.forClass(OrderEntity.class);

        Order receivedOrder = orderService.updateStatus(1l, new Status("order_in_production"));

        Order expectedOrder = Order.builder()
                .id(1l)
                .status("order_in_production")
                .createdAt(LocalDate.of(2000, 1, 1))
                .build();

        assertEquals(expectedOrder, receivedOrder);
        verify(orderRepository).save(orderEntitycaptor.capture());
        assertEquals("order_in_production", orderEntitycaptor.getValue().getStatus());
        verify(cachePort, only()).setKeyWithoutExpirationTime("orderStatus::1",
                orderEntitycaptor.getValue().toDomain());

    }

    @Test
    void when_InitializeStatusCaches_Then_ClearCacheAndSetTheOrdersInRespectiveCaches() {
        List<OrderEntity> orderEntityMocks = List.of(OrderEntity.builder()
                .id(1l)
                .status("order_received")
                .createdAt(LocalDate.of(2000, 1, 1))
                .build(),
                OrderEntity.builder()
                        .id(2l)
                        .status("order_in_production")
                        .createdAt(LocalDate.of(2000, 1, 2))
                        .build());

        when(orderRepository.findAllByStatusIn(any())).thenReturn(orderEntityMocks);

        ArgumentCaptor<List<String>> statusListCaptor = ArgumentCaptor
                .forClass(List.class);

        ArgumentCaptor<Map<String, Object>> keyValueCaptor = ArgumentCaptor
                .forClass(Map.class);

        List<String> expectedStatus = List.of("new", "order_completed", "order_delivered",
                "order_in_production", "order_received", "payment_accepted", "payment_refused", "payment_requested");

        HashMap<String, Object> expectedKeyValue = new HashMap<String, Object>();
        expectedKeyValue.put("orderStatus::1", new Order(1l, "order_received", LocalDate.of(2000, 1, 1)));
        expectedKeyValue.put("orderStatus::2", new Order(2l, "order_in_production", LocalDate.of(2000, 1, 2)));
        List<HashMap<String, Object>> expectedKeyValueList = List.of(expectedKeyValue, new HashMap<String, Object>());
        orderService.initializeOrderActiveStatusCache();

        verify(cachePort, times(1)).clearAllCaches();
        verify(orderRepository).findAllByStatusIn(statusListCaptor.capture());
        assertEquals(expectedStatus.stream().sorted().toList(),
                statusListCaptor.getValue().stream().sorted().toList());
        verify(cachePort, times(2)).setMultiKeyWithoutExpirationTime(keyValueCaptor.capture());
        assertEquals(expectedKeyValueList, keyValueCaptor.getAllValues());

    }

}
