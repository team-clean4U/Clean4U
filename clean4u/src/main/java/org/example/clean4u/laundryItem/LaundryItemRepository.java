package org.example.clean4u.laundryItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LaundryItemRepository extends JpaRepository<LaundryItem, Long> {
    @Query("SELECT li FROM LaundryItem li ORDER BY li.createdAt DESC")
    List<LaundryItem> findAllOrderByCreatedAtDesc();

    @Query("SELECT li FROM LaundryItem li ORDER BY li.createdAt DESC")
    Page<LaundryItem> findAllOrderByCreatedAtDesc(Pageable pageable);

    Page<LaundryItem> findByCategory(LaundryCategory category, Pageable pageable);

    Page<LaundryItem> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT li FROM LaundryItem li WHERE li.name LIKE CONCAT('%', :name, '%') AND li.category = :category ORDER BY li.createdAt DESC")
    Page<LaundryItem> findByNameContainingAndCategory(@Param("category") LaundryCategory category, @Param("name") String name, Pageable pageable);

    Optional<LaundryItem> findByName(String name);
}
