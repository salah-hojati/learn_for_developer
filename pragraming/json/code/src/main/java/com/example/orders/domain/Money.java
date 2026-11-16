package com.example.orders.domain;

import com.example.orders.json.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

@JsonSerialize(using = MoneySerializer.class)
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}