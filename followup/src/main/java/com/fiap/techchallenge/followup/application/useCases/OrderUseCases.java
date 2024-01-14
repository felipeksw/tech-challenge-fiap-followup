package com.fiap.techchallenge.followup.application.useCases;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.DataInputException;
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
            return orders.stream().map(x -> OrderDto.of(x)).collect(Collectors.toList());
        } catch (NotFoundException ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        }
    }

    public OrderUpdateStatusResponseDto updateStatus(OrderUpdateStatusResquestDto updateStatusResquest) {
        try {
            Order order = orderService.updateStatus(updateStatusResquest.getOrderId(),
                    new Status(updateStatusResquest.getStatus()));
            return OrderUpdateStatusResponseDto.of(order, "order status updated");
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new DataInputException(e.getMessage());
        }
    }

}
