package com.example.order_service.client;

import com.example.order_service.dto.CartItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "cart-service"
)
public interface CartClient {

    @GetMapping("/api/cart")
    List<CartItemResponse> getCart(
            @RequestHeader("Authorization") String authorization
    );
  
    @DeleteMapping("/api/cart")
    void clearCart(
        @RequestHeader("Authorization") String authorization
   );

}