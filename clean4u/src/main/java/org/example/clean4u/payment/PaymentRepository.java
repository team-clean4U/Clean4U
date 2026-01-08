package org.example.clean4u.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByImpUid(String impUid);

    Boolean existsByMerchantUid(String merchantUid);

    Optional<Payment> findByMerchantUid(String merchantUid);

    Optional<Payment> findByOrderId(Long orderId);

    Boolean existsByOrderId(Long orderId);
}
