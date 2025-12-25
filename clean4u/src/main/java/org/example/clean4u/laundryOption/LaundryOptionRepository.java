package org.example.clean4u.laundryOption;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LaundryOptionRepository extends JpaRepository<LaundryOption, Long> {
    @Query("SELECT lo FROM LaundryOption lo ORDER BY lo.createdAt DESC")
    List<LaundryOption> findAllOrderByCreatedAtDesc();

    @Query("SELECT lo FROM LaundryOption lo ORDER BY lo.createdAt DESC")
    Page<LaundryOption> findAllOrderByCreatedAtDesc(Pageable pageable);

    Page<LaundryOption> findByIsActive(Boolean isActive, Pageable pageable);

    Page<LaundryOption> findByNameContaining(String name, Pageable pageable);

    Page<LaundryOption> findByNameContainingAndIsActive(String name, Boolean isActive, Pageable pageable);

    Optional<LaundryOption> findByName(String name);
}
