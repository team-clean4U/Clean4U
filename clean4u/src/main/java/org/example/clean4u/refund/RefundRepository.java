package org.example.clean4u.refund;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByPaymentId(Long paymentId);
}
