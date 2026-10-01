package com.example.inventory_service.service;

public interface InventoryService {

    boolean reserveStock(Long productId, Integer quantity);

    boolean releaseStock(Long productId, Integer quantity);
}