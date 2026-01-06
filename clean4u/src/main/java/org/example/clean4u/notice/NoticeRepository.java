package org.example.clean4u.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice,Long> {

    @Query("SELECT n FROM Notice n " +
            "ORDER BY n.createdAt DESC")
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT n.id FROM Notice n " +
            "WHERE n.createdAt < :currentCreatedAt " +
            "OR (n.createdAt = :currentCreatedAt AND n.id < :currentId) " +
            "ORDER BY n.createdAt DESC, n.id DESC " +
            "LIMIT 1")
    Optional<Long> findNextId(
            @Param("currentCreatedAt") Timestamp currentCreatedAt,
            @Param("currentId") Long currentId
    );

    @Query("SELECT n.id FROM Notice n " +
            "WHERE n.createdAt > :currentCreatedAt " +
            "OR (n.createdAt = :currentCreatedAt AND n.id > :currentId) " +
            "ORDER BY n.createdAt ASC, n.id ASC " +
            "LIMIT 1")
    Optional<Long> findPrevId(
            @Param("currentCreatedAt") Timestamp currentCreatedAt,
            @Param("currentId") Long currentId
    );

    @Query("SELECT DISTINCT n FROM Notice n " +
            "LEFT JOIN FETCH n.noticeImages " +
            "WHERE n.id = :noticeId")
    Optional<Notice> findByIdWithImages(@Param("noticeId") Long noticeId);
}
