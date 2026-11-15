package com.example.orders.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebhookEvent {
    private final Map<String, Object> fields = new LinkedHashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) { fields.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getFields() { return fields; }
}