package org.example.clean4u.laundryItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LaundryItemRepository extends JpaRepository<LaundryItem, Long> {
    @Query("SELECT li FROM LaundryItem li ORDER BY li.createdAt DESC")
    List<LaundryItem> findAllOrderByCreatedAtDesc();

    @Query("SELECT li FROM LaundryItem li ORDER BY li.createdAt DESC")
    Page<LaundryItem> findAllOrderByCreatedAtDesc(Pageable pageable);

    Page<LaundryItem> findByCategory(LaundryCategory category, Pageable pageable);

    Page<LaundryItem> findByNameContaining(String name, Pageable pageable);

    Page<LaundryItem> findByNameContainingAndCategory(String name, LaundryCategory category, Pageable pageable);

    Page<LaundryItem> findByIsActive(Boolean isActive, Pageable pageable);

    Page<LaundryItem> findByNameContainingAndIsActive(String name, Boolean isActive, Pageable pageable);

    Page<LaundryItem> findByCategoryAndIsActive(LaundryCategory category, Boolean isActive, Pageable pageable);

    Page<LaundryItem> findByNameContainingAndCategoryAndIsActive(String name, LaundryCategory category, Boolean isActive, Pageable pageable);

    Optional<LaundryItem> findByName(String name);
}
