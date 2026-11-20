package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    private final String id;
    private final String name;
    private final String email;

    @JsonCreator
    public Customer(@JsonProperty("id")    String id,
                    @JsonProperty("name")  String name,
                    @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
}