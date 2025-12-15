package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.customer.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.example.clean4u._core.exception.Exception404;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 전체 조회
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.createdAt DESC", Order.class)
                .getResultList();
    }

    // 주문 검색 조회
    public List<Order> searchOrder(
            OrderStatus status,
            String customerName,
            String phone,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT o FROM Order o JOIN o.customer c");

        boolean hasCondition = false;

        if(status != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("o.status = :status");
            hasCondition = true;
        }

        if(customerName != null && !customerName.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.name LIKE :customerName");
            hasCondition = true;
        }

        if(phone != null && !phone.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.phone LIKE :phone");
            hasCondition = true;
        }

        if(fromDate != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("o.orderDate >= :fromDate");
            hasCondition = true;
        }

        if(toDate != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("o.orderDate <= :toDate");
            hasCondition = true;
        }

        TypedQuery<Order> query = em.createQuery(jpql.toString(), Order.class);

        if(status != null) query.setParameter("status", status);
        if(customerName != null && !customerName.isEmpty()) query.setParameter("customerName", customerName);
        if(phone != null && !phone.isEmpty()) query.setParameter("phone", phone);
        if(fromDate != null) query.setParameter("fromDate", fromDate);
        if(toDate != null) query.setParameter("toDate", toDate);

        return query.getResultList();
    }

    // 주문 상세 조회
    public Order findById(Long id) {
        Order order = em.find(Order.class, id);
        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }
        return order;
    }

    public Long countByCustomerAndStatus(Customer customer, OrderStatus status) {
        return em.createQuery("""
                    SELECT COUNT(o) FROM Order o
                    WHERE o.customer = :customer
                        AND o.status = :status
                    """, Long.class)
                .setParameter("customer", customer)
                .setParameter("status", status)
                .getSingleResult();
    }

    // 주문 생성(저장)
    @Transactional
    public Order save(Order order) {
        em.persist(order);
        return order;
    }

    // 주문 수정
    @Transactional
    public Order updateById(Long id, OrderRequest.UpdateDto updateDto) {
        Order order = em.find(Order.class, id);

        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }

        order.updateOrder(updateDto);
        return order;
    }

    // 주문 삭제
    @Transactional
    public void deleteById(Long id) {
        Order order = em.find(Order.class, id);

        if(order == null) {
            throw new Exception404("해당 주문을 찾을 수 없습니다.");
        }

        em.remove(order);
    }

}
