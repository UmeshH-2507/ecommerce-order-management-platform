package com.example.cart_service.service;

import com.example.cart_service.client.ProductClient;
import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartItemResponse;
import com.example.cart_service.dto.ProductResponse;
import com.example.cart_service.entity.Cart;
import com.example.cart_service.entity.CartItem;
import com.example.cart_service.repository.CartItemRepository;
import com.example.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Override
    public CartItemResponse addToCart(
            AddToCartRequest request,
            Long userId
    ) {

        // 1. Ask Product Service for product details
        ProductResponse product =
                productClient.getProduct(request.getProductId());

        // 2. Check whether product exists and is active
        if (product == null || !product.getActive()) {
            throw new RuntimeException("Product is not available");
        }

        // 3. Find user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        // 4. Check whether product already exists in cart
        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        request.getProductId()
                )
                .orElse(null);

        if (cartItem != null) {

            // Product already exists → increase quantity
            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );

        } else {

            // Product doesn't exist → create new item
            cartItem = new CartItem();

            cartItem.setCartId(cart.getId());
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());

            // Save current product price
            cartItem.setPrice(product.getPrice());
        }

        // 5. Save cart item
        CartItem savedItem = cartItemRepository.save(cartItem);

        // 6. Convert Entity → DTO
        return new CartItemResponse(
                savedItem.getProductId(),
                savedItem.getQuantity(),
                savedItem.getPrice()
        );
    }

    @Override
    public List<CartItemResponse> getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        return cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();
    }

    @Override
    public void removeItem(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        productId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found in cart"
                        )
                );

        cartItemRepository.delete(cartItem);
    }

   @Override
public void clearCart(Long userId) {

    Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

    List<CartItem> cartItems =
            cartItemRepository.findByCartId(cart.getId());

    cartItemRepository.deleteAll(cartItems);
}

@Override
public void updateQuantity(
        Long userId,
        Long productId,
        Integer quantity
) {

    // 1. Find user's cart
    Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() ->
                    new RuntimeException("Cart not found")
            );

    // 2. Find product in cart
    CartItem cartItem = cartItemRepository
            .findByCartIdAndProductId(
                    cart.getId(),
                    productId
            )
            .orElseThrow(() ->
                    new RuntimeException(
                            "Product not found in cart"
                    )
            );

    // 3. Validate quantity
    if (quantity <= 0) {
        throw new RuntimeException(
                "Quantity must be greater than 0"
        );
    }

    // 4. Update quantity
    cartItem.setQuantity(quantity);

    // 5. Save
    cartItemRepository.save(cartItem);
}


}