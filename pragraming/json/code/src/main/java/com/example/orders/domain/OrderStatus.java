package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderStatus {
    PENDING("pending"),
    PAID("paid"),
    SHIPPED("shipped"),
    CANCELLED("cancelled");

    private final String code;

    OrderStatus(String code) { this.code = code; }

    @JsonValue                        // serialize as "pending", not "PENDING"
    public String getCode() { return code; }

    @JsonCreator                      // "pending" -> PENDING
    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + code));
    }
}