package com.example.order_service.service;

import com.example.order_service.client.CartClient;
import com.example.order_service.client.InventoryClient;
import com.example.order_service.client.PaymentClient;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.OutboxEventRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.order_service.dto.CreateOrderRequest;
import java.util.List;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OrderServiceImpl orderService;


  @Test
void shouldThrowExceptionWhenCartIsEmpty() {

    String token = "Bearer test-token";

    when(cartClient.getCart(token))
            .thenReturn(List.of());

    CreateOrderRequest request = new CreateOrderRequest();

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.createOrder(
                    request,
                    4L,
                    token
            )
    );

    assertEquals("Cart is empty", exception.getMessage());
}
}