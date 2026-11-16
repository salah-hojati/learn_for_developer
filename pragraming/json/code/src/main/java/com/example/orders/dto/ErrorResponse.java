package com.example.orders.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}