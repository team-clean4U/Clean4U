package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<Order> searchOrder(Pageable pageable, OrderRequest.SearchDTO searchDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT o FROM Order o JOIN o.customer c");

        boolean hasCondition = false;

        OrderStatus status = searchDTO.getStatus();
        String customerName = searchDTO.getCustomerName();
        String phone = searchDTO.getPhone();
        LocalDate fromDate = searchDTO.getFromDate();
        LocalDate toDate = searchDTO.getToDate();

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
        TypedQuery<Long> countQuery = em.createQuery(
                jpql.toString().replace("SELECT o", "SELECT COUNT(o)"),
                Long.class
        );

        if(status != null) {
            query.setParameter("status", status);
            countQuery.setParameter("status", status);
        }
        if(customerName != null && !customerName.isEmpty()) {
            query.setParameter("customerName", customerName);
            countQuery.setParameter("customerName", customerName);
        }
        if(phone != null && !phone.isEmpty()) {
            query.setParameter("phone", phone);
            countQuery.setParameter("phone", phone);
        }
        if(fromDate != null) {
            query.setParameter("fromDate", fromDate);
            countQuery.setParameter("fromDate", fromDate);
        }
        if(toDate != null) {
            query.setParameter("toDate", toDate);
            countQuery.setParameter("toDate", toDate);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Order> content = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
