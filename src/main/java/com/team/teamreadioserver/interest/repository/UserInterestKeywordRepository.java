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

    @Query(value = """
    SELECT 
        DATE_FORMAT(uik.created_at, :format) AS period, 
        ik.interest_keyword, 
        COUNT(*) AS count
    FROM user_interest_keyword uik
    JOIN interest_keyword ik ON uik.interest_keyword_id = ik.interest_keyword_id
    WHERE uik.created_at BETWEEN :startDate AND :endDate
      AND uik.status = 'ACTIVE'
    GROUP BY period, ik.interest_keyword
    ORDER BY period, count DESC
""", nativeQuery = true)
    List<Object[]> findKeywordTrend(@Param("startDate") LocalDateTime start,
                                    @Param("endDate") LocalDateTime end,
                                    @Param("format") String format);
}
