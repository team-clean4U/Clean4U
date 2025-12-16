package org.example.clean4u.laundryOption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LaundryOptionRepository extends JpaRepository<LaundryOption, Long> {
    List<LaundryOption> findByIsActive(Boolean isActive);

    List<LaundryOption> findByNameContaining(String name);

    Optional<LaundryOption> findByName(String name);
}
