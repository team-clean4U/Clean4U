package org.example.clean4u.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Boolean existsByMerchantUid(String merchantUid);

    Optional<Payment> findByOrderId(Long orderId);

    Boolean existsByOrderId(Long orderId);
}
