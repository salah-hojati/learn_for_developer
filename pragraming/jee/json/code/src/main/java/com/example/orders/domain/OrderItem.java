package com.example.orders.domain;

import com.example.orders.json.SkuDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

public class OrderItem {
    @JsonDeserialize(using = SkuDeserializer.class)
    private String sku;

    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}