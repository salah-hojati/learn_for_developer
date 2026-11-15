package com.example.orders.web;

import com.example.orders.dto.ErrorResponse;
import com.example.orders.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        Throwable cursor = ex;
        while (cursor != null) {
            if (cursor instanceof ValidationException ve) {
                return error(400, "validation_error", ve.getMessage());
            }
            if (cursor instanceof InvalidFormatException ife) {
                return error(400, "invalid_format", ife.getOriginalMessage());
            }
            if (cursor instanceof JsonMappingException jme) {
                String msg = jme.getOriginalMessage() != null
                        ? jme.getOriginalMessage() : jme.getMessage();
                return error(400, "invalid_json", msg);
            }
            cursor = cursor.getCause();
        }
        return error(500, "internal_error", "An unexpected error occurred");
    }

    private Response error(int status, String code, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(code, message))
                .build();
    }
}