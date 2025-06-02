package com.team.teamreadioserver.statistics.repository;

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
    @Query("""
        SELECT c.contentId, COUNT(c)
        FROM ClickLog c
        WHERE c.contentType = :type
        AND c.clickedAt BETWEEN :startDate AND :endDate
        GROUP BY c.contentId
        ORDER BY COUNT(c) DESC
        """)
    List<Object[]> findTopClickedContent(
            @Param("type") String contentType,
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end
    );
}