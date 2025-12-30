package org.example.clean4u.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<Order> searchOrder(Pageable pageable, OrderRequest.SearchDTO searchDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT o FROM Order o JOIN o.customer c WHERE o.status <> :cancelled");

        OrderStatus status = searchDTO.getStatus();
        String customerName = searchDTO.getCustomerName();
        String phone = searchDTO.getPhone();
        LocalDate fromDate = searchDTO.getFromDate();
        LocalDate toDate = searchDTO.getToDate();

        if(status != null) {
            jpql.append(" AND o.status = :status");
        }

        if(customerName != null && !customerName.isEmpty()) {
            jpql.append(" AND c.name LIKE CONCAT('%', :customerName, '%')");
        }

        if(phone != null && !phone.isEmpty()) {
            jpql.append(" c.phone LIKE CONCAT('%', :phone, '%')");
        }

        if(fromDate != null) {
            jpql.append(" o.orderDate >= :fromDate");;
        }

        if(toDate != null) {
            jpql.append(" o.orderDate <= :toDate");
        }

        StringBuilder listJpql = new StringBuilder(jpql);
        if(pageable.getSort().isSorted()) {
            listJpql.append(" ORDER BY ");

            boolean first = true;
            for(Sort.Order sortOrder : pageable.getSort()) {
                if(!first) {
                    listJpql.append(", ");
                }
                listJpql.append("o.")
                        .append(sortOrder.getProperty())
                        .append(sortOrder.isAscending() ? " ASC" : " DESC");
                first = false;
            }
        }

        TypedQuery<Order> query = em.createQuery(listJpql.toString(), Order.class);
        TypedQuery<Long> countQuery = em.createQuery(
                jpql.toString().replace("SELECT o", "SELECT COUNT(o)"),
                Long.class
        );

        query.setParameter("cancelled", OrderStatus.CANCELLED);
        countQuery.setParameter("cancelled", OrderStatus.CANCELLED);

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
