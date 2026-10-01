package com.example.order_service.controller;

import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.security.UserPrincipal;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication,
            @RequestHeader("Authorization") String authorization
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return orderService.createOrder(
                request,
                principal.getUserId(),
                authorization
        );
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return orderService.getOrder(
                orderId,
                principal.getUserId()
        );
    }

    @GetMapping("/my-orders")
    public List<OrderResponse> getMyOrders(
            Authentication authentication
    ) {
        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return orderService.getMyOrders(
                principal.getUserId()
        );
    }
}