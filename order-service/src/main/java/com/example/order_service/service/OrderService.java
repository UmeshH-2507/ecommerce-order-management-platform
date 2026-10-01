package com.example.order_service.service;

import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(
            CreateOrderRequest request,
            Long userId,
            String authorization
    );

    OrderResponse getOrder(
            Long orderId,
            Long userId
    );

    List<OrderResponse> getMyOrders(
            Long userId
    );



    
}