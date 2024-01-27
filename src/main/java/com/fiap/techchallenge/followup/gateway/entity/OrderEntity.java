package com.fiap.techchallenge.followup.gateway.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;

import com.fiap.techchallenge.followup.domain.Order;

import jakarta.persistence.CascadeType;
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
    private Long customerId;
    private String client;
    private BigDecimal price;
    private String status;
    private String paymentMethod;
    @CreationTimestamp
    private LocalDate createdAt;

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
