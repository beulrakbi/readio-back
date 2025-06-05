package com.team.teamreadioserver.interest.repository;


import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import com.team.teamreadioserver.interest.enums.InterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    // 특정 사용자의 활성화된 관심 카테고리를 찾는 메서드
    List<UserInterestCategory> findByUserAndStatus(com.team.teamreadioserver.user.entity.User user, InterestStatus status);

    // 주어진 관심 카테고리 ID 목록에 해당하는 활성화된 사용자 관심 카테고리를 찾는 메서드 (같은 관심사 가진 사용자 찾기)
    List<UserInterestCategory> findByInterestCategory_InterestIdInAndStatus(Set<Long> interestCategoryIds, InterestStatus status);
}