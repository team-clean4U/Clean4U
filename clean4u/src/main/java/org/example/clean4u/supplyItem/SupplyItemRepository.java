package org.example.clean4u.supplyItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {
    @Query("SELECT si FROM SupplyItem si ORDER BY si.createdAt DESC")
    List<SupplyItem> findAllOrderByCreatedAtDesc();

    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity <= si.safetyStock ORDER BY si.createdAt DESC")
    List<SupplyItem> findLowStockItems();

    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity > si.safetyStock ORDER BY si.createdAt DESC")
    List<SupplyItem> findSafetyStockItems();

    List<SupplyItem> findByNameContaining(String name);

    @Query("SELECT si FROM SupplyItem si WHERE si.name LIKE CONCAT('%', :name, '%') AND si.stockQuantity <= si.safetyStock ORDER BY si.createdAt DESC")
    List<SupplyItem> findByNameContainingAndLowStock(@Param("name") String name);

    @Query("SELECT si FROM SupplyItem si WHERE si.name LIKE CONCAT('%', :name, '%') AND si.stockQuantity > si.safetyStock ORDER BY si.createdAt DESC")
    List<SupplyItem> findByNameContainingAndSafetyStock(@Param("name") String name);

    Optional<SupplyItem> findByName(String name);
}
