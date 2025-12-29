package org.example.clean4u.orderStatusHistory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.order.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class OrderStatusHistoryImpl implements OrderStatusHistoryCustom {
    private final EntityManager em;

    @Override
    public Page<OrderStatusHistory> searchHistories(Pageable pageable, OrderRequest.SearchDTO searchDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT h FROM OrderStatusHistory h JOIN h.order o JOIN o.customer c");

        boolean hasCondition = false;

        String customerName = searchDTO.getCustomerName();
        String phone = searchDTO.getPhone();
        LocalDate fromDate = searchDTO.getFromDate();
        LocalDate toDate = searchDTO.getToDate();

        if(customerName != null && !customerName.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.name LIKE :customerName");
            hasCondition = true;
        }

        if(phone != null && !phone.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.phone LIKE :phone");
            hasCondition = true;
        }

        if(fromDate != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("h.createdAt >= :fromDate");
            hasCondition = true;
        }

        if(toDate != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("h.createdAt <= :toDate");
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

        List<OrderStatusHistory> content = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
