package com.example.cart_service.service;

import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartItemResponse;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface CartService {

    CartItemResponse addToCart(AddToCartRequest request, Long userId);

    List<CartItemResponse> getCart(Long userId);

    void updateQuantity(Long userId, Long productId, Integer quantity);

    void removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}