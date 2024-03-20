package com.fiap.techchallenge.followup.gateway.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fiap.techchallenge.followup.application.enums.StatusEnum;
import com.fiap.techchallenge.followup.domain.Order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client", nullable = false, columnDefinition = "varchar(255) NOT NULL")
    private String client;

    @Column(name = "customer_id", columnDefinition = "varchar(255) DEFAULT NULL")
    private String customerId;

    @Column(name = "payment_method", nullable = false, columnDefinition = "varchar(255) NOT NULL")
    private String paymentMethod;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(38,2) DEFAULT 0.00")
    private BigDecimal price;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) DEFAULT 'new'")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "datetime DEFAULT current_timestamp()")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "orders", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItemsEntity> orderItems;

    public Order toDomain() {
        return Order.builder()
                .id(this.id)
                .status(this.status)
                .createdAt(createdAt)
                .build();
    }

}
