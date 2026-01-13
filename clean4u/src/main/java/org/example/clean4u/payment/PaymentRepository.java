package org.example.clean4u.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
    Optional<Payment> findByImpUid(String impUid);

    Boolean existsByMerchantUid(String merchantUid);

    Optional<Payment> findByMerchantUid(String merchantUid);

    Optional<Payment> findByOrderId(Long orderId);

    Boolean existsByOrderId(Long orderId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithOrder(@Param("paymentId") Long paymentId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Payment p WHERE p.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}