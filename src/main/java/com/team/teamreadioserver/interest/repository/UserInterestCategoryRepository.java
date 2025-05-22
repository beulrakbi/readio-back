package com.team.teamreadioserver.interest.repository;


import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestCategoryRepository extends JpaRepository<UserInterestCategory, Long> {

    List<UserInterestCategory> findByUser_UserId(String userId);

    void deleteByUser_UserIdAndInterestCategory_InterestId(String userId, Long interestId);

    boolean existsByUser_UserIdAndInterestCategory_InterestId(String userId, Long interestId);

}