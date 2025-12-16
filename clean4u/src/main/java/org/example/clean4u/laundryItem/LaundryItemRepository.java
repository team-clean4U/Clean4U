package org.example.clean4u.laundryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LaundryItemRepository extends JpaRepository<LaundryItem, Long> {
    @Query("SELECT li FROM LaundryItem li ORDER BY li.createdAt DESC")
    List<LaundryItem> findAllOrderByCreatedAtDesc();

    List<LaundryItem> findByCategory(LaundryCategory category);

    List<LaundryItem> findByNameContaining(String name);

    Optional<LaundryItem> findByName(String name);
}
