package com.example.orders.dto;

import com.example.orders.domain.Money;
import com.example.orders.domain.OrderStatus;
import com.example.orders.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private final String id;
    private final String customerId;
    private final OrderStatus status;
    private final Money total;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
                timezone = "UTC")
    private final Instant createdAt;

    private final PaymentMethod payment;

    public OrderResponse(String id, String customerId, OrderStatus status,
                         Money total, Instant createdAt, PaymentMethod payment) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.payment = payment;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public Money getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public PaymentMethod getPayment() { return payment; }

    @JsonIgnore                       // debug helper, never serialized
    public String getSummary() { return id + " / " + status; }
}