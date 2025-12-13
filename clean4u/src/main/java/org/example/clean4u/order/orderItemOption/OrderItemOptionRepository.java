package org.example.clean4u.order.orderItemOption;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception404;
import org.example.clean4u.order.orderItem.OrderItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemOptionRepository {
    private final EntityManager em;

    @Transactional
    public OrderItemOption save(OrderItemOption orderItemOption) {
        em.persist(orderItemOption);
        return orderItemOption;
    }

    public List<OrderItemOption> findByOrderItemId(Long id) {
        return em.createQuery("""
                        SELECT o FROM OrderItemOption o
                        WHERE o.orderItem.id = :id
                        ORDER BY o.createdAt DESC
                        """
                        , OrderItemOption.class)
                .setParameter("id", id)
                .getResultList();
    }

    // 주문 단위 전체 삭제
    public void deleteByOrderId(Long id) {
        em.createQuery("""
                DELETE FROM OrderItemOption o
                WHERE o.orderItem.id IN (
                    SELECT oi.id FROM OrderItem oi
                    WHERE oi.order.id = :id
                )
                """)
                .setParameter("id", id)
                .executeUpdate();
    }

    // 주문 품목 하나에 대한 옵션만 삭제
    public void deleteByOrderItemId(Long id) {
        em.createQuery("""
                DELETE FROM OrderItemOption o
                WHERE o.orderItem.id = :id
                """)
                .setParameter("id", id)
                .executeUpdate();

    }

}
