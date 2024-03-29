package com.fiap.techchallenge.followup.presentation.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.techchallenge.followup.application.useCases.OrderUseCases;
import com.fiap.techchallenge.followup.presentation.dtos.ErrorResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResponseDto;
import com.fiap.techchallenge.followup.presentation.dtos.OrderUpdateStatusResquestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/order")
@Tag(name = "Pedidos", description = "Operações para gerenciamento de pedidos")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private final OrderUseCases orderUseCases;

    @Operation(summary = "Recuperar todas as ordens ativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OrderDto.class))
            }),
            @ApiResponse(responseCode = "500", description = "Problemas internos durante a recuperação das ordens ativas", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<OrderDto>> findAllOrdersStatusWithStatusActive() {
        return ResponseEntity.status(HttpStatus.OK).body(orderUseCases.findAllOrderStatusWithActiveStatus());
    }

    @Operation(summary = "Atualiza o cache com as ordens que possuem status ativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cache atualizado", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OrderDto.class))
            }),
            @ApiResponse(responseCode = "500", description = "Problemas internos durante a atualização do cache", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PostMapping(value = "/refresh-cache")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> refreshOrderStatusCache() {
        orderUseCases.refreshOrderStatusCache();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
