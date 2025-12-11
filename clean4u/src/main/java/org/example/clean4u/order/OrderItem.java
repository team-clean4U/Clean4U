package org.example.clean4u.order;

import jakarta.persistence.*;
import org.example.clean4u.time.BaseTimeEntity;

@Entity
@Table(name = "order_item_tb")
public class OrderItem extends BaseTimeEntity {
    // 하나의 Order 에 있는 구체적인 항목

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
