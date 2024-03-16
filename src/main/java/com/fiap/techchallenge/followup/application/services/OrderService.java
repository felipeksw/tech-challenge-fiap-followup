package com.fiap.techchallenge.followup.application.services;

import java.util.List;

import com.fiap.techchallenge.followup.domain.Order;
import com.fiap.techchallenge.followup.domain.Status;

public interface OrderService {

    List<Order> findAllOrderStatusWithActiveStatus();

    Order findOrderOnPaymentStatusCacheById(Long orderId);

    Order updateStatus(Long orderId, Status newStatus);

    void initializeOrderActiveStatusCache();
}
