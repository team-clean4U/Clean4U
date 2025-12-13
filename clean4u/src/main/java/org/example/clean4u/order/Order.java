package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.customer.Customer;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.time.BaseTimeEntity;

import java.time.LocalDate;

@Entity
@Table(name = "order_tb")
@NoArgsConstructor
@Data
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "memo", length = 50)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private Employee editor;

    @Builder
    public Order(Customer customer, OrderStatus status, Long totalPrice, LocalDate orderDate, String memo, Employee editor) {
        this.customer = customer;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate == null ? LocalDate.now() : orderDate;
        this.memo = memo;
        this.editor = editor;
    }

    public void updateOrder(OrderRequest.UpdateDto updateDto, Long totalPrice, Employee editor) {
        this.status = updateDto.getStatus();
        this.totalPrice = totalPrice;
        this.orderDate = updateDto.getOrderDate() == null ? LocalDate.now() : updateDto.getOrderDate();
        this.memo = updateDto.getMemo();
        this.editor = editor;
    }

    public void updateStatus(OrderStatus newStatus) {
        if(newStatus == null) {
            throw new IllegalArgumentException("주문 상태는 null일 수 없습니다.");
        }
        this.status = newStatus;
    }
}
