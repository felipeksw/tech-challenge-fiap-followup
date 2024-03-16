package com.fiap.techchallenge.followup.application.useCases;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fiap.techchallenge.followup.application.services.OrderService;
import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;
import com.fiap.techchallenge.followup.domain.exceptions.BadRequestException;
import com.fiap.techchallenge.followup.domain.exceptions.BaseHttpException.RequestDataDto;
import com.fiap.techchallenge.followup.domain.exceptions.InternalServerErrorException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.domain.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.followup.presentation.dtos.OrderDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResquestDto;

@ExtendWith(SpringExtension.class)
public class OrderUseCasesTest {

    @InjectMocks
    private OrderUseCases orderUseCases;

    @Mock
    private OrderService orderService;

    @Test
    void when_FindAllActiveStatusAndHAveAError_Then_ThrowAException() {

        RuntimeException exceptionToThrow = new RuntimeException("Error during find all active status");
        when(orderService.findAllOrderStatusWithActiveStatus()).thenThrow(exceptionToThrow);

        InternalServerErrorException receivedException = assertThrowsExactly(InternalServerErrorException.class,
                () -> orderUseCases.findAllOrderStatusWithActiveStatus());

        InternalServerErrorException expectedException = new InternalServerErrorException(exceptionToThrow.getMessage(),
                new RequestDataDto(null));
        assertAll("Check all information about the excetpion",
                () -> assertEquals(expectedException.getMessage(), receivedException.getMessage()),
                () -> assertEquals(expectedException.getRequestData(), receivedException.getRequestData()),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, receivedException.getStatusCode()));

    }

    @Test
    void when_FindAllActiveStatus_Then_ReturnAOrderDtoList() {

        List<Order> orderListMock = List.of(new Order(1l, "order_received", LocalDate.of(2000, 1, 1)),
                new Order(2l, "order_received", LocalDate.of(2000, 1, 1)));

        when(orderService.findAllOrderStatusWithActiveStatus()).thenReturn(orderListMock);

        List<OrderDto> receivedOrderDtoList = orderUseCases.findAllOrderStatusWithActiveStatus();

        List<OrderDto> expectedOrderDtoList = List.of(new OrderDto(1l, "order_received", LocalDate.of(2000, 1, 1)),
                new OrderDto(2l, "order_received", LocalDate.of(2000, 1, 1)));

        assertEquals(expectedOrderDtoList, receivedOrderDtoList);

    }

    @Test
    void when_UpdateStatusOfAOrderDoesntExists_Then_ThrowAResourceNotFoundException() {

        NotFoundException exceptionToThrow = new NotFoundException("Order id not found");
        when(orderService.updateStatus(any(), any())).thenThrow(exceptionToThrow);

        OrderUpdateStatusResquestDto orderUpdateStatusResquestMock = OrderUpdateStatusResquestDto.builder()
                .orderId(1l)
                .status("order_received")
                .build();

        ResourceNotFoundException receivedException = assertThrowsExactly(ResourceNotFoundException.class,
                () -> orderUseCases.updateStatus(orderUpdateStatusResquestMock));

        ResourceNotFoundException expectedException = new ResourceNotFoundException(exceptionToThrow.getMessage(),
                new RequestDataDto(orderUpdateStatusResquestMock));

        assertAll("Check all information about the excetpion",
                () -> assertEquals(expectedException.getMessage(), receivedException.getMessage()),
                () -> assertEquals(expectedException.getRequestData(), receivedException.getRequestData()),
                () -> assertEquals(HttpStatus.NOT_FOUND, receivedException.getStatusCode()));

    }

    @Test
    void when_UpdateStatusWithInvalidData_Then_ThrowABadRequestException() {

        OrderUpdateStatusResquestDto orderUpdateStatusResquestMock = new OrderUpdateStatusResquestDto();
        BadRequestException receivedException = assertThrowsExactly(BadRequestException.class,
                () -> orderUseCases.updateStatus(orderUpdateStatusResquestMock));

        BadRequestException expectedException = new BadRequestException(
                "Invalid Status: status must not be null or empty",
                new RequestDataDto(orderUpdateStatusResquestMock));

        assertAll("Check all information about the excetpion",
                () -> assertEquals(expectedException.getMessage(), receivedException.getMessage()),
                () -> assertEquals(expectedException.getRequestData(), receivedException.getRequestData()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, receivedException.getStatusCode()));

    }

    @Test
    void when_UpdateStatusAndHaveAInternalError_Then_ThrowAInternalServerErrorException() {

        RuntimeException exceptionToThrow = new RuntimeException("Error during the updating status");
        when(orderService.updateStatus(any(), any())).thenThrow(exceptionToThrow);

        OrderUpdateStatusResquestDto orderUpdateStatusResquestMock = new OrderUpdateStatusResquestDto(1l,
                "order_received");

        InternalServerErrorException receivedException = assertThrowsExactly(InternalServerErrorException.class,
                () -> orderUseCases.updateStatus(orderUpdateStatusResquestMock));

        InternalServerErrorException expectedException = new InternalServerErrorException(exceptionToThrow.getMessage(),
                new RequestDataDto(orderUpdateStatusResquestMock));

        assertAll("Check all information about the excetpion",
                () -> assertEquals(expectedException.getMessage(), receivedException.getMessage()),
                () -> assertEquals(expectedException.getRequestData(), receivedException.getRequestData()),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, receivedException.getStatusCode()));

    }

    @Test
    void when_UpdateStatus_Then_ReturnTheOrderUpdatedReponse() {

        OrderUpdateStatusResquestDto orderUpdateStatusResquestMock = new OrderUpdateStatusResquestDto(1l,
                "order_completed");
        Order orderUpdatedMock = new Order(1l, "order_completed", LocalDate.of(2000, 1, 1));

        when(orderService.updateStatus(1l, new Status("order_completed"))).thenReturn(orderUpdatedMock);

        OrderUpdateStatusResponseDto receivedOrderUpdatedResponse = orderUseCases
                .updateStatus(orderUpdateStatusResquestMock);

        OrderUpdateStatusResponseDto expectedOrderUpdatedResponse = new OrderUpdateStatusResponseDto(1l,
                "order_completed",
                "order status updated");

        assertEquals(expectedOrderUpdatedResponse, receivedOrderUpdatedResponse);
    }

    @Test
    void when_RefreshOrderStatusCacheAndHaveAError_Then_ThrowAException() {

        RuntimeException exceptionToThrow = new RuntimeException("Error during refresh order status cache");
        doThrow(exceptionToThrow).when(orderService).initializeOrderActiveStatusCache();

        InternalServerErrorException receivedException = assertThrowsExactly(InternalServerErrorException.class,
                () -> orderUseCases.refreshOrderStatusCache());

        InternalServerErrorException expectedException = new InternalServerErrorException(exceptionToThrow.getMessage(),
                new RequestDataDto(null));
        assertAll("Check all information about the excetpion",
                () -> assertEquals(expectedException.getMessage(), receivedException.getMessage()),
                () -> assertEquals(expectedException.getRequestData(), receivedException.getRequestData()),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, receivedException.getStatusCode()));

    }

    @Test
    void when_RefreshOrderStatusCache_Then_InitializeTheCache() {

        orderUseCases.refreshOrderStatusCache();

        verify(orderService, only()).initializeOrderActiveStatusCache();
    }

}
