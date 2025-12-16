package org.example.clean4u.supplyItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {
    @Query("SELECT si FROM SupplyItem si WHERE si.stockQuantity <= si.safetyStock")
    List<SupplyItem> findLowStockItems();

    List<SupplyItem> findByNameContaining(String name);

    Optional<SupplyItem> findByName(String name);
}
