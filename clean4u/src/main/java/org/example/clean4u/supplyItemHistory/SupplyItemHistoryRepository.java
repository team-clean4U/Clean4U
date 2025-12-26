package org.example.clean4u.supplyItemHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SupplyItemHistoryRepository extends JpaRepository<SupplyItemHistory, Long> {
    @Query(value = "SELECT " +
            "MAX(sih.supply_item_history_id) as first_history_id, " +
            "COUNT(*) as item_count " +
            "FROM supply_item_history_tb sih " +
            "WHERE (:type IS NULL OR sih.type = :type) " +
            "AND (:fromDate IS NULL OR DATE(sih.created_at) >= :fromDate) " +
            "AND (:toDate IS NULL OR DATE(sih.created_at) <= :toDate) " +
            "GROUP BY DATE(sih.created_at), sih.type, sih.employee_id " +
            "ORDER BY MAX(sih.created_at) DESC",
            countQuery = "SELECT COUNT(DISTINCT CONCAT(DATE(sih.created_at), '_', sih.type, '_', sih.employee_id)) " +
                    "FROM supply_item_history_tb sih " +
                    "WHERE (:type IS NULL OR sih.type = :type) " +
                    "AND (:fromDate IS NULL OR DATE(sih.created_at) >= :fromDate) " +
                    "AND (:toDate IS NULL OR DATE(sih.created_at) <= :toDate)",
            nativeQuery = true)
    Page<Object[]> findAllWithFilters(
            @Param("type") String type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query("SELECT sih FROM SupplyItemHistory sih " +
            "WHERE DATE(sih.createdAt) = DATE((SELECT sih2.createdAt FROM SupplyItemHistory sih2 WHERE sih2.id = :historyId)) " +
            "AND sih.type = (SELECT sih3.type FROM SupplyItemHistory sih3 WHERE sih3.id = :historyId) " +
            "AND sih.employee.id = (SELECT sih4.employee.id FROM SupplyItemHistory sih4 WHERE sih4.id = :historyId) " +
            "ORDER BY sih.id")
    List<SupplyItemHistory> findGroupedHistories(@Param("historyId") Long historyId);
}
