package com.example.orders.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/webhook")
public class WebhookServlet extends HttpServlet {

    // A single, reused mapper — never create one per request.
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            WebhookEvent event = MAPPER.readValue(req.getInputStream(), WebhookEvent.class);
            MAPPER.writeValue(resp.getOutputStream(), event);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            MAPPER.writeValue(resp.getOutputStream(), Map.of("error", "invalid JSON"));
        }
    }
}