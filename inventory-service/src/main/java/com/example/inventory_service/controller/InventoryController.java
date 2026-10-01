package com.example.inventory_service.controller;

import com.example.inventory_service.dto.ReserveStockRequest;
import com.example.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


       @Value("${server.port}")
       private String serverPort;

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(
            @Valid @RequestBody ReserveStockRequest request) {

                System.out.println(
        ">>> Inventory request handled by port: " + serverPort
);

        boolean reserved = inventoryService.reserveStock(
                request.getProductId(),
                request.getQuantity()
        );

        if (!reserved) {
            return ResponseEntity.badRequest()
                    .body("Insufficient stock");
        }

        return ResponseEntity.ok("Stock reserved successfully");
    }

    @PostMapping("/release")
public ResponseEntity<String> releaseStock(
        @Valid @RequestBody ReserveStockRequest request) {

    boolean released = inventoryService.releaseStock(
            request.getProductId(),
            request.getQuantity()
    );

    if (!released) {
        return ResponseEntity
                .badRequest()
                .body("Unable to release stock");
    }

    return ResponseEntity.ok("Stock released successfully");
}


}