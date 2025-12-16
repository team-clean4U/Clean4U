package org.example.clean4u.laundryOption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LaundryOptionRepository extends JpaRepository<LaundryOption, Long> {
    @Query("SELECT lo FROM LaundryOption lo ORDER BY lo.createdAt DESC")
    List<LaundryOption> findAllOrderByCreatedAtDesc();

    List<LaundryOption> findByIsActive(Boolean isActive);

    List<LaundryOption> findByNameContaining(String name);

    List<LaundryOption> findByNameContainingAndIsActive(String name, Boolean isActive);
}
