package com.team.teamreadioserver.interest.repository;

import com.team.teamreadioserver.interest.entity.InterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestCategoryRepository extends JpaRepository<InterestCategory, Long> {
    boolean existsByInterestCategory(String interestCategory);
}