package com.fiap.techchallenge.followup.gateway.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orderitems")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String description;
    private Long quantity;
    @ManyToOne
    @JoinColumn(name = "orderId")
    private OrderEntity orders;

}
