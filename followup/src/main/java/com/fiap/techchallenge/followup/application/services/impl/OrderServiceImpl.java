package com.fiap.techchallenge.followup.application.services.impl;

import static com.fiap.techchallenge.followup.util.ConstantsUtil.ORDER_NOT_FOUND;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.gateway.entity.OrderEntity;
import com.fiap.techchallenge.followup.gateway.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final CacheManager cacheManager;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private final ObjectMapper objectMapper;

    private final String ORDER_STATUS_CACHE_PREFIX_KEY = "orderStatus::";

    @Override
    public List<Order> findAllWithActiveStatus() {

        Set<String> orderStatusKeys = redisTemplate.keys(ORDER_STATUS_CACHE_PREFIX_KEY + "*");
        List<Order> ordersWithActiveStatus = (List<Order>) redisTemplate.opsForValue().multiGet(orderStatusKeys)
                .stream()
                .map(object -> objectMapper.convertValue(object, Order.class)).sorted(Comparator.comparing(Order::id))
                .collect(Collectors.toList());

        return ordersWithActiveStatus;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, Status newStatus) {

        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        Order orderSaved = orderEntity.isPresent() ? orderEntity.get().toDomain() : null;

        if (Objects.isNull(orderSaved)) {
            throw new NotFoundException(ORDER_NOT_FOUND);
        }

        if (!orderSaved.status().newStatusIsValid(newStatus)) {
            throw new InvalidDataException(
                    "Status " + orderSaved.status().value() + " cannot be changed to " + newStatus.value());
        }

        OrderEntity orderEntityToBeSaved = orderEntity.get();
        orderEntityToBeSaved.setStatus(newStatus.value());
        Order resultOrder = orderRepository.save(orderEntityToBeSaved).toDomain();

        if (resultOrder.status().value().equalsIgnoreCase(StatusEnum.FINALIZADO.toString())) {
            redisTemplate.opsForValue().set(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(), resultOrder, 3,
                    TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().set(ORDER_STATUS_CACHE_PREFIX_KEY + resultOrder.id(), resultOrder);
        }

        return resultOrder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeOrderActiveStatusCache() {
        // TODO: Remover Mock
        List<Order> orderToInsert = new ArrayList<>();
        for (Integer i = 0; i < 10; i++) {

            orderToInsert.add(new Order(Integer.toUnsignedLong(i), "recebido",
                    LocalDate.now().plusDays(Integer.toUnsignedLong(i))));
        }
        orderRepository.saveAll(orderToInsert.stream().map(OrderEntity::fromDomain).toList());

        List<Status> searchedStatus = StatusEnum.getActiveStatus().stream().map(Status::new).toList();
        var orderEntityList = orderRepository.findAllByStatusIn(searchedStatus.stream().map(Status::value).toList());
        List<Order> orderList = orderEntityList.stream().map(order -> order.toDomain()).toList();

        redisTemplate.opsForValue().multiSet(
                orderList.stream().collect(
                        Collectors.toMap(order -> ORDER_STATUS_CACHE_PREFIX_KEY + order.id(), order -> order)));
    }

}
