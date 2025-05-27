package com.team.teamreadioserver.postReview.repository;

import com.team.teamreadioserver.postReview.entity.PostReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReviewRepository extends JpaRepository<PostReview, Integer> {
}
