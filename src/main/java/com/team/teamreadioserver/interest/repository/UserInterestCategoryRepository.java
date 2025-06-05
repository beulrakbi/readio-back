package com.team.teamreadioserver.interest.repository;


import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserInterestCategoryRepository extends JpaRepository<UserInterestCategory, Long> {

    List<UserInterestCategory> findByUser_UserId(String userId);

    void deleteByUser_UserIdAndInterestCategory_InterestId(String userId, Long interestId);

    boolean existsByUser_UserIdAndInterestCategory_InterestId(String userId, Long interestId);

    // 1. 전체 집계 (2개 파라미터)
    @Query(value = """
    SELECT 
        i.interest_category AS label, 
        COUNT(*) AS count
    FROM user_interest ui
    JOIN interest i ON ui.interest_id = i.interest_id
    WHERE ui.created_at BETWEEN :start AND :end
      AND ui.status = 'ACTIVE'
    GROUP BY i.interest_category
""", nativeQuery = true)
    List<Object[]> findCategoryTrendTotal(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    // 2. 기간별 (3개 파라미터: format 포함)
    @Query(value = """
    SELECT 
        DATE_FORMAT(ui.created_at, :format) AS period,
        i.interest_category AS label,
        COUNT(*) AS count
    FROM user_interest ui
    JOIN interest i ON ui.interest_id = i.interest_id
    WHERE ui.created_at BETWEEN :start AND :end
      AND ui.status = 'ACTIVE'
    GROUP BY period, i.interest_category
""", nativeQuery = true)
    List<Object[]> findCategoryTrend(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("format") String format);

}