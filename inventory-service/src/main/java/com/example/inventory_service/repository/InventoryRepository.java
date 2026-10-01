package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.quantity = i.quantity - :quantity
        WHERE i.productId = :productId
        AND i.quantity >= :quantity
    """)
    int reserveStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );


    @Modifying
@Query("""
    UPDATE Inventory i
    SET i.quantity = i.quantity + :quantity
    WHERE i.productId = :productId
""")
int releaseStock(
        @Param("productId") Long productId,
        @Param("quantity") Integer quantity
);
}