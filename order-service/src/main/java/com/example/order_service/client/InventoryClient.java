package com.example.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "inventory-service"
)
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    String reserveStock(@RequestBody Map<String, Object> request);

    @PostMapping("/api/inventory/release")
String releaseStock(@RequestBody Map<String, Object> request);


}