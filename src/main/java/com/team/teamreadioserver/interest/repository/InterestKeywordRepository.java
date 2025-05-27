package com.team.teamreadioserver.interest.repository;

import com.team.teamreadioserver.interest.entity.InterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestKeywordRepository extends JpaRepository<InterestKeyword, Long> {
    boolean existsByInterestKeyword(String interestKeyword);
}
