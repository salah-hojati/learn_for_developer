package com.example.orders.json;

import com.example.orders.domain.Money;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money money, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("amount", money.getAmount().toPlainString());
        gen.writeStringField("currency", money.getCurrency());
        gen.writeEndObject();
    }
}