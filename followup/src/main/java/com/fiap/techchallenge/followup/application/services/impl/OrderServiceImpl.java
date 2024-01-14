package com.fiap.techchallenge.followup.application.services.impl;

import static com.fiap.techchallenge.followup.util.ConstantsUtil.ORDER_NOT_FOUND;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.gateway.entity.OrderEntity;
import com.fiap.techchallenge.followup.gateway.repository.OrderRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAllWithActiveStatus() {
        List<Status> searchedStatus = StatusEnum.getActiveStatus().stream().map(Status::new).toList();
        var orderList = orderRepository.findAllByStatusIn(searchedStatus.stream().map(Status::value).toList());
        return orderList.stream().map(order -> order.toDomain()).collect(Collectors.toList());
    }

    @Override
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

        return orderRepository.save(orderEntityToBeSaved).toDomain();
    }

}
