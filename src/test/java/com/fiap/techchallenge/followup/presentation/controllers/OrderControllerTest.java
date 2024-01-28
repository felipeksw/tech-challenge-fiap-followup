package com.fiap.techchallenge.followup.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge.followup.application.useCases.OrderUseCases;
import com.fiap.techchallenge.followup.domain.exceptions.BaseHttpException.RequestDataDto;
import com.fiap.techchallenge.followup.domain.exceptions.InternalServerErrorException;
import com.fiap.techchallenge.followup.domain.exceptions.NotFoundException;
import com.fiap.techchallenge.followup.domain.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResquestDto;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockBean
    private OrderUseCases orderUseCases;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void when_GetAllActiveStatusAndHaveAInternalError_Then_ReturnStatusInternalErrorAndMessage() throws Exception {
        InternalServerErrorException exceptionToBeThrow = new InternalServerErrorException(
                "Error during find all active status", new RequestDataDto(null));
        when(orderUseCases.findAllWithActiveStatus()).thenThrow(exceptionToBeThrow);

        ErrorResponseDto expectedResult = new ErrorResponseDto(null, exceptionToBeThrow.getMessage());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/order").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResult)));
    }

    @Test
    void when_PutUpdateStatusAndOrderIdDoentExist_Then_ReturnStatusNotFoundAndMessageRequest() throws Exception {

        OrderUpdateStatusResquestDto orderUpdateStatusRequest = OrderUpdateStatusResquestDto.builder()
                .orderId(1l)
                .status("pronto")
                .build();

        ResourceNotFoundException exceptionToBeThrow = new ResourceNotFoundException(
                "Order id not found", new RequestDataDto(orderUpdateStatusRequest));

        when(orderUseCases.updateStatus(any())).thenThrow(exceptionToBeThrow);

        ErrorResponseDto expectedResult = new ErrorResponseDto(orderUpdateStatusRequest,
                exceptionToBeThrow.getMessage());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/order").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateStatusRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResult)));
    }

}
