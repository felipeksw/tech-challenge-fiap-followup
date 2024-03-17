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

    @ManyToOne
    @JoinColumn(name = "order_id", columnDefinition = "bigint(20) DEFAULT NULL")
    private OrderEntity orders;

    @Column(name = "description", columnDefinition = "varchar(255) DEFAULT NULL")
    private String description;

    @Column(name = "additional_info", columnDefinition = "varchar(255) DEFAULT NULL")
    private String additionaiInfo;

    @Column(name = "quantity", columnDefinition = "bigint(20) DEFAULT NULL")
    private Long quantity;

    @Column(name = "product_id", columnDefinition = "bigint(20) DEFAULT NULL")
    private Long productId;

}
