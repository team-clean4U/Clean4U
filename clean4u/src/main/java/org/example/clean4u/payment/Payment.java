package org.example.clean4u.payment;

import jakarta.persistence.*;
import lombok.*;
import org.example.clean4u.order.Order;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "payment_tb")
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Column(name = "imp_uid", unique = true, nullable = false, updatable = false)
    private String impUid;

    @Column(name = "merchant_uid", unique = true, nullable = false, updatable = false)
    private String merchantUid;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public Payment(Order order, String impUid, String merchantUid, Integer amount, PaymentStatus paymentStatus) {
        this.order = order;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public void updateStatus(PaymentStatus paymentStatus) {
        if(paymentStatus != null) {
            this.paymentStatus = paymentStatus;
        }
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.PAID;
    }

    public boolean isRefund() {
        return this.paymentStatus == PaymentStatus.REFUND;
    }
}
