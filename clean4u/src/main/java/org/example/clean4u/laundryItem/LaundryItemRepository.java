package org.example.clean4u.laundryItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LaundryItemRepository extends JpaRepository<LaundryItem, Long> {
    List<LaundryItem> findByCategory(LaundryCategory category);

    List<LaundryItem> findByNameContaining(String name);

    Optional<LaundryItem> findByName(String name);
}
