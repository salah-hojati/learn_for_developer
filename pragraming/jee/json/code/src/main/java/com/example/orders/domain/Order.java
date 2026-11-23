package com.example.orders.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final PaymentMethod paymentMethod;
    private final Instant createdAt;
    private final OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items,
                 PaymentMethod paymentMethod, Instant createdAt, OrderStatus status) {
        this.id = id;
        this.customer = customer;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
}