package com.team.teamreadioserver.interest.repository;

import com.team.teamreadioserver.interest.entity.UserInterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface UserInterestKeywordRepository extends JpaRepository<UserInterestKeyword, Long> {

    List<UserInterestKeyword> findByUser_UserId(String userId);

    void deleteByUser_UserIdAndInterestKeyword_InterestKeywordId(String userId, Long interestKeywordId);

    boolean existsByUser_UserIdAndInterestKeyword_InterestKeywordId(String userId, Long interestKeywordId);

    // 1. 전체 집계 (2개 파라미터)
    @Query(value = """
    SELECT 
        ik.interest_keyword AS label,
        COUNT(*) AS count
    FROM user_interest_keyword uik
    JOIN interest_keyword ik ON uik.interest_keyword_id = ik.interest_keyword_id
    WHERE uik.created_at BETWEEN :start AND :end
      AND uik.status = 'ACTIVE'
    GROUP BY ik.interest_keyword
""", nativeQuery = true)
    List<Object[]> findKeywordTrendTotal(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    // 2. 기간별 (3개 파라미터: format 포함)
    @Query(value = """
    SELECT 
        DATE_FORMAT(uik.created_at, :format) AS period, 
        ik.interest_keyword AS label, 
        COUNT(*) AS count
    FROM user_interest_keyword uik
    JOIN interest_keyword ik ON uik.interest_keyword_id = ik.interest_keyword_id
    WHERE uik.created_at BETWEEN :start AND :end
      AND uik.status = 'ACTIVE'
    GROUP BY period, ik.interest_keyword
""", nativeQuery = true)
    List<Object[]> findKeywordTrend(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("format") String format);
}