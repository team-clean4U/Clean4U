package org.example.clean4u.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
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
    private OrderStatus status = OrderStatus.RECEIVED;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDate orderDate = LocalDate.now();

    @Column(name = "memo", length = 50)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private Employee editor;

    @Builder
    public Order(Customer customer, OrderStatus status, Integer totalPrice, LocalDate orderDate, String memo, Employee editor) {
        this.customer = customer;
        this.status = status == null ? OrderStatus.RECEIVED : status;
        this.totalPrice = totalPrice ;
        this.orderDate = orderDate == null ? LocalDate.now() : orderDate;
        this.memo = memo;
        this.editor = editor;
    }

    public void updateOrder(OrderRequest.UpdateDTO updateDto) {
        this.status = updateDto.getStatus();
        this.memo = updateDto.getMemo();
    }

    public void updatePrice(Integer totalPrice) {
        if(totalPrice == null) {
            throw new Exception400("주문 금액 입력은 필수입니다.");
        }
        this.totalPrice = totalPrice;
    }

    public void updateEditor(Employee editor) {
        if(editor == null) {
            throw new Exception400("작성자 입력은 필수입니다.");
        }
        this.editor = editor;
    }

    public void updateStatus(OrderStatus newStatus) {
        if(newStatus == null) {
            throw new Exception400("주문 상태 입력은 필수입니다.");
        }
        this.status = newStatus;
    }
}
