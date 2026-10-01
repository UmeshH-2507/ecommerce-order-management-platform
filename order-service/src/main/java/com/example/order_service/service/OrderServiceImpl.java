package com.example.order_service.service;

import com.example.order_service.client.CartClient;
import com.example.order_service.client.InventoryClient;
import com.example.order_service.client.PaymentClient;
import com.example.order_service.dto.CartItemResponse;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderItemResponse;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.dto.PaymentRequest;
import com.example.order_service.dto.PaymentResponse;
import com.example.order_service.dto.ShippingAddressRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.OrderStatus;
import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.entity.ShippingAddress;
import com.example.order_service.exception.InsufficientStockException;
import com.example.order_service.exception.PaymentFailedException;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OutboxEventRepository outboxEventRepository;



    @Override
    @Transactional 
public OrderResponse createOrder(
        CreateOrderRequest request,
        Long userId,
        String authorization
) {

    List<CartItemResponse> cartItems =
            cartClient.getCart(authorization);

           

    if (cartItems == null || cartItems.isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }

    // Reserve inventory for every cart item
    for (CartItemResponse cartItem : cartItems) {

        Map<String, Object> inventoryRequest = Map.of(
                "productId", cartItem.getProductId(),
                "quantity", cartItem.getQuantity()
        );

        try {
            inventoryClient.reserveStock(inventoryRequest);
        } catch (Exception e) {
            throw new InsufficientStockException(
        "Insufficient stock for product: "
                + cartItem.getProductId()
);
        }
    }

    BigDecimal totalAmount = cartItems.stream()
            .map(item ->
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    )
            )
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order();
    order.setUserId(userId);
    order.setTotalAmount(totalAmount);
    order.setStatus(OrderStatus.PENDING_PAYMENT);
    order.setCreatedAt(LocalDateTime.now());

    ShippingAddressRequest addressRequest =
            request.getShippingAddress();

    ShippingAddress shippingAddress = new ShippingAddress();

    shippingAddress.setFullName(addressRequest.getFullName());
    shippingAddress.setPhone(addressRequest.getPhone());
    shippingAddress.setAddressLine(addressRequest.getAddressLine());
    shippingAddress.setCity(addressRequest.getCity());
    shippingAddress.setState(addressRequest.getState());
    shippingAddress.setPostalCode(addressRequest.getPostalCode());
    shippingAddress.setCountry(addressRequest.getCountry());

    order.setShippingAddress(shippingAddress);

    Order savedOrder = orderRepository.save(order);


PaymentRequest paymentRequest = new PaymentRequest(
        savedOrder.getId(),
        savedOrder.getTotalAmount(),
        request.isSimulatePaymentFailure()
);

    PaymentResponse paymentResponse =
        paymentClient.processPayment(paymentRequest);

  if (!"SUCCESS".equals(paymentResponse.getStatus())) {

    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);

    // Release reserved inventory
    for (CartItemResponse cartItem : cartItems) {

        Map<String, Object> releaseRequest = Map.of(
                "productId", cartItem.getProductId(),
                "quantity", cartItem.getQuantity()
        );

        inventoryClient.releaseStock(releaseRequest);
    }

    throw new PaymentFailedException("Payment failed");
}

      order.setStatus(OrderStatus.PAID);
       orderRepository.save(order);

         OutboxEvent event = new OutboxEvent();
event.setEventType("ORDER_CREATED");
event.setAggregateId(savedOrder.getId());
event.setPayload(
        "{\"orderId\":" + savedOrder.getId()
        + ",\"userId\":" + savedOrder.getUserId()
        + ",\"totalAmount\":" + savedOrder.getTotalAmount()
        + ",\"status\":\"" + savedOrder.getStatus() + "\"}"
);
event.setCreatedAt(LocalDateTime.now());
event.setPublished(false);

outboxEventRepository.save(event);

    List<OrderItem> orderItems = cartItems.stream()
            .map(cartItem -> {

                OrderItem orderItem = new OrderItem();

                orderItem.setOrderId(savedOrder.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());

                return orderItem;
            })
            .toList();

    List<OrderItem> savedOrderItems =
            orderItemRepository.saveAll(orderItems);

    cartClient.clearCart(authorization);

    return mapToOrderResponse(
            savedOrder,
            savedOrderItems
    );
}

    @Override
    public OrderResponse getOrder(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        // Make sure this order belongs to the logged-in user
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to access this order");
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        return mapToOrderResponse(order, orderItems);
    }

    @Override
    public List<OrderResponse> getMyOrders(Long userId) {

        List<Order> orders =
                orderRepository.findByUserId(userId);

        return orders.stream()
                .map(order -> {

                    List<OrderItem> orderItems =
                            orderItemRepository.findByOrderId(order.getId());

                    return mapToOrderResponse(order, orderItems);
                })
                .toList();
    }

    private OrderResponse mapToOrderResponse(
            Order order,
            List<OrderItem> orderItems
    ) {

        List<OrderItemResponse> itemResponses =
                orderItems.stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getProductId(),
                                        item.getQuantity(),
                                        item.getPrice()
                                )
                        )
                        .toList();

        ShippingAddress address =
                order.getShippingAddress();

        ShippingAddressRequest shippingAddress =
                new ShippingAddressRequest(
                        address.getFullName(),
                        address.getPhone(),
                        address.getAddressLine(),
                        address.getCity(),
                        address.getState(),
                        address.getPostalCode(),
                        address.getCountry()
                );

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                shippingAddress,
                itemResponses
        );
    }
}