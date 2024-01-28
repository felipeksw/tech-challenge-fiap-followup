package com.fiap.techchallenge.followup.application.services.impl;

import static com.fiap.techchallenge.followup.util.ConstantsUtil.ORDER_NOT_FOUND;

import java.util.Comparator;
import java.util.List;
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

    private final String ORDER_STATUS_CACHE_PREFIX_KEY = "orderStatus::";

    @Override
    public List<Order> findAllWithActiveStatus() {

        Set<String> orderStatusKeys = cachePort.getAllKeysByNamePattern(ORDER_STATUS_CACHE_PREFIX_KEY + "*");
        return cachePort.getAllDataByKeys(orderStatusKeys)
                .stream()
                .map(object -> objectMapper.convertValue(object, Order.class)).sorted(Comparator.comparing(Order::id))
                .toList();

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

        List<Status> searchedStatus = StatusEnum.getActiveStatus().stream().map(Status::new).toList();
        List<OrderEntity> orderEntityList = orderRepository
                .findAllByStatusIn(searchedStatus.stream().map(Status::value).toList());
        List<Order> orderList = orderEntityList.stream().map(OrderEntity::toDomain).toList();

        cachePort.setMultiKeyWithoutExpirationTime(orderList.stream().collect(
                Collectors.toMap(order -> ORDER_STATUS_CACHE_PREFIX_KEY + order.id(), order -> order)));

    }

    @Override
    public Order syncOrderToOrderStatusCache(Long orderId) {

        OrderEntity orderEntity = findOrderEntityById(orderId);

        Order orderToSync = orderEntity.toDomain();

        setOrderOnCache(orderToSync);

        return orderToSync;

    }

    private OrderEntity findOrderEntityById(Long orderId) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderId);

        if (orderEntity.isEmpty()) {
            throw new NotFoundException(ORDER_NOT_FOUND);
        }
        return orderEntity.get();
    }

    private void setOrderOnCache(Order resultOrder) {

        if (resultOrder.status().value().equalsIgnoreCase(StatusEnum.FINALIZADO.toString())) {
            cachePort.setKeyWithExpirationTimeInMinutes(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(), resultOrder,
                    3);
        } else {
            cachePort.setKeyWithoutExpirationTime(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(), resultOrder);
        }
    }
}
