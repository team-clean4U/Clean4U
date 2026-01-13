package org.example.clean4u.payment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<Payment> searchPayments(Pageable pageable, PaymentRequest.SearchDTO searchDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT p FROM Payment p JOIN p.order o JOIN o.customer c");
        boolean hasCondition = false;

        String customerName = searchDTO.getCustomerName();
        String phone = searchDTO.getPhone();
        String merchantUid = searchDTO.getMerchantUid();
        PaymentStatus status = searchDTO.getPaymentStatus();
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        if(searchDTO.getFromDate() != null) {
            fromDateTime = searchDTO.getFromDate().atStartOfDay();
        }
        if(searchDTO.getToDate() != null) {
            toDateTime = searchDTO.getToDate().atTime(23, 59, 59);
        }

        if(customerName != null && !customerName.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.name LIKE CONCAT('%', :customerName, '%')");
            hasCondition = true;
        }

        if(phone != null && !phone.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("c.phone LIKE CONCAT('%', :phone, '%')");
            hasCondition = true;
        }

        if(merchantUid != null && !merchantUid.isEmpty()) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("p.merchantUid = :merchantUid");
            hasCondition = true;
        }

        if(status != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("p.paymentStatus = :paymentStatus");
            hasCondition = true;
        }

        if(fromDateTime != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("p.createdAt >= :fromDateTime");
            hasCondition = true;
        }

        if(toDateTime != null) {
            jpql.append(hasCondition ? " AND " : " WHERE ").append("o.createdAt <= :toDateTime");
            hasCondition = true;
        }

        StringBuilder listJpql = new StringBuilder(jpql);
        if(pageable.getSort().isSorted()) {
            listJpql.append(" ORDER BY ");

            boolean first = true;
            for(Sort.Order sortOrder : pageable.getSort()) {
                if(!first) {
                    listJpql.append(", ");
                }
                listJpql.append("p.")
                        .append(sortOrder.getProperty())
                        .append(sortOrder.isAscending() ? " ASC" : " DESC");
                first = false;
            }
        }

        TypedQuery<Payment> query = em.createQuery(listJpql.toString(), Payment.class);
        TypedQuery<Long> countQuery = em.createQuery(
                jpql.toString().replace("SELECT p", "SELECT COUNT(p)"),
                Long.class
        );

        if(status != null) {
            query.setParameter("paymentStatus", status);
            countQuery.setParameter("paymentStatus", status);
        }
        if(customerName != null && !customerName.isEmpty()) {
            query.setParameter("customerName", customerName);
            countQuery.setParameter("customerName", customerName);
        }
        if(phone != null && !phone.isEmpty()) {
            query.setParameter("phone", phone);
            countQuery.setParameter("phone", phone);
        }
        if(merchantUid != null && !merchantUid.isEmpty()) {
            query.setParameter("merchantUid", merchantUid);
            countQuery.setParameter("merchantUid", merchantUid);
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

        List<Payment> content = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
