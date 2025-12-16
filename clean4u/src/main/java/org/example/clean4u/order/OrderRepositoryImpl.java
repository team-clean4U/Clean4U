package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Order> searchOrder(OrderStatus status, String customerName, String phone, LocalDate fromDate, LocalDate toDate) {
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
}
