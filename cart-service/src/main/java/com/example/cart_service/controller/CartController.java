package com.example.cart_service.controller;

import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartItemResponse;
import com.example.cart_service.security.UserPrincipal;
import com.example.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public CartItemResponse addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return cartService.addToCart(
                request,
                principal.getUserId()
        );
    }

    @GetMapping
    public List<CartItemResponse> getCart(
            Authentication authentication
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return cartService.getCart(
                principal.getUserId()
        );
    }


    @PutMapping("/items/{productId}")
public String updateQuantity(
        @PathVariable Long productId,
        @RequestParam Integer quantity,
        Authentication authentication
) {
    UserPrincipal principal =
            (UserPrincipal) authentication.getPrincipal();

    cartService.updateQuantity(
            principal.getUserId(),
            productId,
            quantity
    );

    return "Cart quantity updated successfully";
}

    @DeleteMapping("/items/{productId}")
    public String removeItem(
            @PathVariable Long productId,
            Authentication authentication
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        cartService.removeItem(
                principal.getUserId(),
                productId
        );

        return "Item removed from cart";
    }
    
    @DeleteMapping
    public String clearCart(Authentication authentication) {

    UserPrincipal principal =
            (UserPrincipal) authentication.getPrincipal();

    cartService.clearCart(principal.getUserId());

    return "Cart cleared successfully";
}


}