package com.team.teamreadioserver.interest.repository;

import com.team.teamreadioserver.interest.entity.UserInterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface UserInterestKeywordRepository extends JpaRepository<UserInterestKeyword, Long> {

    List<UserInterestKeyword> findByUser_UserId(String userId);

    void deleteByUser_UserIdAndInterestKeyword_InterestKeywordId(String userId, Long interestKeywordId);

    boolean existsByUser_UserIdAndInterestKeyword_InterestKeywordId(String userId, Long interestKeywordId);

}
