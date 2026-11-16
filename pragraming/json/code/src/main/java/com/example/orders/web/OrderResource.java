package com.example.orders.web;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.service.OrderService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService service = OrderService.getInstance();

    @POST
    public Response create(CreateOrderRequest request) {
        OrderResponse created = service.create(request);
        return Response.created(URI.create("/api/orders/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        OrderResponse found = service.find(id);
        return found == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(found).build();
    }
}