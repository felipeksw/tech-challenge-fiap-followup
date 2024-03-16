package com.fiap.techchallenge.followup.application.services.impl;

import static com.fiap.techchallenge.followup.util.ConstantsUtil.ORDER_NOT_FOUND;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.gateway.entity.OrderEntity;
import com.fiap.techchallenge.followup.gateway.port.CachePort;
import com.fiap.techchallenge.followup.gateway.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CachePort cachePort;

    private final ObjectMapper objectMapper;

    private static final String ORDER_STATUS_CACHE_PREFIX_KEY = "orderStatus::";
    private static final String ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY = "orderPaymentStatus::";

    @Override
    public List<Order> findAllOrderStatusWithActiveStatus() {

        Set<String> orderStatusKeys = cachePort.getAllKeysByNamePattern(ORDER_STATUS_CACHE_PREFIX_KEY + "*");
        return cachePort.getAllDataByKeys(orderStatusKeys)
                .stream()
                .map(object -> objectMapper.convertValue(object, Order.class)).sorted(Comparator.comparing(Order::id))
                .toList();

    }

    @Override
    public Order findOrderOnPaymentStatusCacheById(Long orderId) {

        Object orderObject = cachePort.getValueByKey(ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY + orderId);

        if (orderObject == null) {
            throw new NotFoundException(ORDER_NOT_FOUND + ". Nenhuma ordem foi encontrada no cache: "
                    + ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY);
        }

        return objectMapper.convertValue(orderObject, Order.class);

    }

    @Override
    @Transactional
    public Order updateStatus(Long orderId, Status newStatus) {

        OrderEntity orderEntity = findOrderEntityById(orderId);

        Order orderSaved = orderEntity.toDomain();

        if (!orderSaved.status().newStatusIsValid(newStatus)) {
            throw new InvalidDataException(
                    "Status " + orderSaved.status().value() + " cannot be changed to " + newStatus.value());
        }

        OrderEntity orderEntityToBeSaved = orderEntity;
        orderEntityToBeSaved.setStatus(newStatus.value());
        Order resultOrder = orderRepository.save(orderEntityToBeSaved).toDomain();

        setOrderOnCache(resultOrder);

        return resultOrder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void initializeOrderActiveStatusCache() {

        cachePort.clearAllCaches();

        List<String> searchedStatus = Arrays.stream(StatusEnum.values())
                .map(statusEnum -> statusEnum.name().toLowerCase()).toList();

        List<OrderEntity> orderEntityList = orderRepository.findAllByStatusIn(searchedStatus);

        List<Order> orderList = orderEntityList.stream().map(OrderEntity::toDomain).toList();

        Map<String, List<Order>> ordersGroupByStatusType = orderList.stream()
                .collect(Collectors.groupingBy(order -> order.status().isPaymentStatus() ? "payment" : "production"));

        cachePort.setMultiKeyWithoutExpirationTime(
                ordersGroupByStatusType.getOrDefault("production", Collections.emptyList()).stream().collect(
                        Collectors.toMap(order -> ORDER_STATUS_CACHE_PREFIX_KEY + order.id(), order -> order)));

        cachePort.setMultiKeyWithoutExpirationTime(
                ordersGroupByStatusType.getOrDefault("payment", Collections.emptyList()).stream().collect(
                        Collectors.toMap(order -> ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY + order.id(), order -> order)));

    }

    private OrderEntity findOrderEntityById(Long orderId) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderId);

        if (orderEntity.isEmpty()) {
            throw new NotFoundException(ORDER_NOT_FOUND);
        }
        return orderEntity.get();
    }

    private void setOrderOnCache(Order resultOrder) {

        StatusEnum status = StatusEnum.valueOfIgnoreCase(resultOrder.status().value());

        if (resultOrder.status().isPaymentStatus()) {

            if (status.equals(StatusEnum.PAYMENT_ACCEPTED) || status.equals(StatusEnum.PAYMENT_REFUSED)) {
                cachePort.setKeyWithExpirationTimeInMinutes(ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY + resultOrder.id(),
                        resultOrder,
                        5);
            } else {
                cachePort.setKeyWithoutExpirationTime(ORDER_PAYMENT_STATUS_CACHE_PREFIX_KEY + resultOrder.id(),
                        resultOrder);
            }

        } else {

            if (status.equals(StatusEnum.ORDER_DELIVERED)) {
                cachePort.setKeyWithExpirationTimeInMinutes(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(),
                        resultOrder,
                        3);
            } else {
                cachePort.setKeyWithoutExpirationTime(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(), resultOrder);
            }
        }

    }
}
