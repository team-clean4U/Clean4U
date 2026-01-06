package org.example.clean4u.supplyItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {
    @Query("SELECT si FROM SupplyItem si ORDER BY si.createdAt DESC")
    List<SupplyItem> findAllOrderByCreatedAtDesc();

    @Query("SELECT si FROM SupplyItem si ORDER BY si.createdAt DESC")
    Page<SupplyItem> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity <= si.safetyStock ORDER BY si.createdAt DESC")
    Page<SupplyItem> findLowStockItems(Pageable pageable);

    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity <= si.safetyStock ORDER BY si.createdAt DESC")
    List<SupplyItem> findAllLowStockItems();

    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity > si.safetyStock ORDER BY si.createdAt DESC")
    Page<SupplyItem> findSafetyStockItems(Pageable pageable);

    Page<SupplyItem> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT si FROM SupplyItem si WHERE si.name LIKE CONCAT('%', :name, '%') AND si.stockQuantity <= si.safetyStock ORDER BY si.createdAt DESC")
    Page<SupplyItem> findByNameContainingAndLowStock(@Param("name") String name, Pageable pageable);

    @Query("SELECT si FROM SupplyItem si WHERE si.name LIKE CONCAT('%', :name, '%') AND si.stockQuantity > si.safetyStock ORDER BY si.createdAt DESC")
    Page<SupplyItem> findByNameContainingAndSafetyStock(@Param("name") String name, Pageable pageable);

    Optional<SupplyItem> findByName(String name);
}
