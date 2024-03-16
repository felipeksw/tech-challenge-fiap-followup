package com.fiap.techchallenge.followup.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.techchallenge.followup.application.useCases.OrderUseCases;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/payment")
@Tag(name = "Pagamento", description = "Operações para gerenciamento de status de pagamento do pedido")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentController {

    private final OrderUseCases orderUseCases;

    @Operation(summary = "Recupera um status de pagamento do cache de pagamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status de pagamento encontrado no cache", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OrderDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Nenhum pedido encontrado", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "500", description = "Problemas internos durante a busca pelo status de pagamento do pedido", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping("{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderDto> getPaymentStatusOrderById(
            @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderUseCases.findOrderOnPaymentStatusCacheById(orderId));
    }

}