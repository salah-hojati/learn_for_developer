package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditCardPayment extends PaymentMethod {
    private String last4;

    // Accepted from the client but NEVER written back to the response.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cardNumber;

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public String describe() { return "Credit card ending in " + last4; }
}