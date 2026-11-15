package com.example.orders.dto;

import com.example.orders.domain.Customer;
import com.example.orders.domain.OrderItem;
import com.example.orders.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequest {
    private Customer customer;
    private List<OrderItem> items;
    private PaymentMethod payment;    // polymorphic!
    private Map<String, String> metadata;

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public PaymentMethod getPayment() { return payment; }
    public void setPayment(PaymentMethod payment) { this.payment = payment; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}