package com.example.orders.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.util.regex.Pattern;

public class SkuDeserializer extends JsonDeserializer<String> {
    private static final Pattern SKU = Pattern.compile("^SKU-[A-Z0-9]{4}$");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getText();
        if (value == null || !SKU.matcher(value).matches()) {
            throw InvalidFormatException.from(p,
                "Invalid SKU format. Expected 'SKU-' followed by 4 uppercase alphanumerics.",
                value, String.class);
        }
        return value;
    }
}