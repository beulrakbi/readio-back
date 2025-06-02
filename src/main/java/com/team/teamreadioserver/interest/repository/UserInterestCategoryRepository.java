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

    @Query(value = """
    SELECT 
        DATE_FORMAT(ui.created_at, :format) AS period, 
        ic.interest_category, 
        COUNT(*) AS count
    FROM user_interest ui
    JOIN interest_category ic ON ui.interest_id = ic.interest_id
    WHERE ui.created_at BETWEEN :startDate AND :endDate
      AND ui.status = 'ACTIVE'
    GROUP BY period, ic.interest_category
    ORDER BY period, count DESC 
""", nativeQuery = true)
    List<Object[]> findCategoryTrend(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("format") String format);
}