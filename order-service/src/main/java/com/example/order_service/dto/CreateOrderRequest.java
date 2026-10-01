package com.example.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull
    private Long cartId;

    @Valid
    @NotNull
    private ShippingAddressRequest shippingAddress;

    private boolean simulatePaymentFailure;
}