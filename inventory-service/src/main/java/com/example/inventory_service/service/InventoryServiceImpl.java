package com.example.inventory_service.service;

import com.example.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public boolean reserveStock(Long productId, Integer quantity) {

        int updatedRows =
                inventoryRepository.reserveStock(productId, quantity);

        return updatedRows == 1;
    }
   
@Override
@Transactional
public boolean releaseStock(Long productId, Integer quantity) {

    int updatedRows =
            inventoryRepository.releaseStock(productId, quantity);

    System.out.println(
            "RELEASE STOCK → productId=" + productId
            + ", quantity=" + quantity
            + ", updatedRows=" + updatedRows
    );

    return updatedRows == 1;
}

}