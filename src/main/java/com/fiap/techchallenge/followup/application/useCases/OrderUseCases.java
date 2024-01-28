package com.fiap.techchallenge.followup.application.useCases;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.BadRequestException;
import com.fiap.techchallenge.followup.domain.exceptions.BaseHttpException.RequestDataDto;
import com.fiap.techchallenge.followup.domain.exceptions.InternalServerErrorException;
import com.fiap.techchallenge.followup.domain.exceptions.InvalidDataException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.domain.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.followup.presentation.dtos.OrderDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResquestDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderUseCases {

    private final OrderService orderService;

    public List<OrderDto> findAllWithActiveStatus() {
        try {
            List<Order> orders = orderService.findAllWithActiveStatus();
            return orders.stream().map(OrderDto::of).toList();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), new RequestDataDto(null));
        }
    }

    public OrderUpdateStatusResponseDto updateStatus(OrderUpdateStatusResquestDto updateStatusResquest) {
        try {
            Order order = orderService.updateStatus(updateStatusResquest.getOrderId(),
                    new Status(updateStatusResquest.getStatus()));
            return OrderUpdateStatusResponseDto.of(order, "order status updated");
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), new RequestDataDto(updateStatusResquest));
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage(), new RequestDataDto(updateStatusResquest));
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), new RequestDataDto(updateStatusResquest));
        }
    }

    public void refreshOrderStatusCache() {
        try {
            orderService.initializeOrderActiveStatusCache();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), new RequestDataDto(null));
        }
    }

    public OrderDto syncOrderToOrderStatusCache(Long orderId) {
        try {
            Order order = orderService.syncOrderToOrderStatusCache(orderId);
            return OrderDto.of(order);
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), new RequestDataDto(Map.of("orderId", orderId)));
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), new RequestDataDto(Map.of("orderId", orderId)));
        }
    }

}
