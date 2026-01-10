package org.example.clean4u.refund;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.payment.Payment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "refund_tb")
@NoArgsConstructor
@Data
public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "status", nullable = false)
    private RefundStatus status = RefundStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public Refund(Payment payment, String reason, RefundStatus status) {
        this.payment = payment;
        this.reason = reason;
        this.status = status;
    }

    public void approve() {
        this.status = RefundStatus.APPROVED;
    }

    public void reject(String rejectReason) {
        this.status = RefundStatus.CANCELLED;
    }

    public boolean isPending() {
        return this.status == RefundStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == RefundStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == RefundStatus.CANCELLED;
    }
}
