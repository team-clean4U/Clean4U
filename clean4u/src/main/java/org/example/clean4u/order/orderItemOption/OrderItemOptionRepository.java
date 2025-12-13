package org.example.clean4u.order.orderItemOption;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemOptionRepository {
    private final EntityManager em;
}
