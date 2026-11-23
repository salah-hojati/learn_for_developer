package com.example.orders.domain;

public class PayPalPayment extends PaymentMethod {
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String describe() { return "PayPal account " + email; }
}