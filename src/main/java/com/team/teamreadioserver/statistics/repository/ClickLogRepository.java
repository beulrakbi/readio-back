package com.team.teamreadioserver.statistics.repository;

import com.team.teamreadioserver.statistics.dto.ClickedContentDTO;
import com.team.teamreadioserver.statistics.entity.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
    //클릭 통계 쿼리
    @Query(value = """
    SELECT c.content_id, c.content_type, COUNT(*) AS click_count
    FROM click_log c
    WHERE c.content_type = :type
    AND c.clicked_at BETWEEN :startDate AND :endDate
    GROUP BY c.content_id, c.content_type
    ORDER BY click_count DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> findTopClickedContentWithPaging(
            @Param("type") String contentType,
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(DISTINCT c.content_id)
    FROM click_log c
    WHERE c.content_type = :type
    AND c.clicked_at BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    long countTotalClickedContent(
            @Param("type") String contentType,
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end
    );


}