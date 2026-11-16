package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCardPayment.class, name = "credit_card"),
    @JsonSubTypes.Type(value = PayPalPayment.class,     name = "paypal")
})
public abstract class PaymentMethod {
    public abstract String describe();
}