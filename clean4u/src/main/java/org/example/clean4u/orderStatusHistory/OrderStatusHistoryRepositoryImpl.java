package org.example.clean4u.orderStatusHistory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.order.OrderRequest;
import org.example.clean4u.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderStatusHistoryRepositoryImpl implements OrderStatusHistoryRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<OrderStatusHistory> searchHistories(Pageable pageable, OrderRequest.SearchDTO searchDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT h FROM OrderStatusHistory h JOIN h.order o JOIN o.customer c");

        boolean hasCondition = false;

        String customerName = searchDTO.getCustomerName();
        String phone = searchDTO.getPhone();
        OrderStatus status = searchDTO.getStatus();
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        if(searchDTO.getFromDate() != null) {
            fromDateTime = searchDTO.getFromDate().atStartOfDay();
        }
        if(searchDTO.getToDate() != null) {
            toDateTime = searchDTO.getToDate().atTime(23, 59, 59);
        }

        if(status != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("h.status = :status");
            hasCondition = true;
        }

        if(customerName != null && !customerName.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.name LIKE CONCAT('%', :customerName, '%')");
            hasCondition = true;
        }

        if(phone != null && !phone.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.phone LIKE CONCAT('%', :phone, '%')");
            hasCondition = true;
        }

        if(fromDateTime != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("h.createdAt >= :fromDateTime");
            hasCondition = true;
        }

        if(toDateTime != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("h.createdAt <= :toDateTime");
            hasCondition = true;
        }

        StringBuilder listJpql = new StringBuilder(jpql);
        if(pageable.getSort().isSorted()) {
            listJpql.append(" ORDER BY ");

            boolean first = true;
            for(Sort.Order sort : pageable.getSort()) {
                if(!first) {
                    listJpql.append(", ");
                }
                listJpql.append("h.")
                        .append(sort.getProperty())
                        .append(sort.isAscending() ? " ASC" : " DESC");
                first = false;
            }
        }

        TypedQuery<OrderStatusHistory> query = em.createQuery(listJpql.toString(), OrderStatusHistory.class);
        TypedQuery<Long> countQuery = em.createQuery(
                jpql.toString().replace("SELECT h", "SELECT COUNT(h)"),
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
        if(fromDateTime != null) {
            query.setParameter("fromDateTime", fromDateTime);
            countQuery.setParameter("fromDateTime", fromDateTime);
        }
        if(toDateTime != null) {
            query.setParameter("toDateTime", toDateTime);
            countQuery.setParameter("toDateTime", toDateTime);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<OrderStatusHistory> content = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
