package com.example.orders.service;

import com.example.orders.domain.*;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.exception.ValidationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {
    private static final OrderService INSTANCE = new OrderService();
    public static OrderService getInstance() { return INSTANCE; }

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public OrderResponse create(CreateOrderRequest req) {
        if (req.getCustomer() == null) throw new ValidationException("customer is required");
        if (req.getItems() == null || req.getItems().isEmpty())
            throw new ValidationException("items must not be empty");
        if (req.getPayment() == null) throw new ValidationException("payment is required");

        String id = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(id, req.getCustomer(), req.getItems(),
                req.getPayment(), Instant.now(), OrderStatus.PENDING);
        store.put(id, order);
        return toResponse(order);
    }

    public OrderResponse find(String id) {
        Order o = store.get(id);
        return o == null ? null : toResponse(o);
    }

    private OrderResponse toResponse(Order o) {
        BigDecimal sum = o.getItems().stream()
                .map(i -> i.getUnitPrice() == null ? BigDecimal.ZERO
                          : i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderResponse(o.getId(), o.getCustomer().getId(), o.getStatus(),
                new Money(sum, "USD"), o.getCreatedAt(), o.getPaymentMethod());
    }
}