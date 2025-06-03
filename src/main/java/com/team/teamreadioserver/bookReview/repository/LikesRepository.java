package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.ReviewLike; // ReviewLike 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // @Param import

public interface LikesRepository extends JpaRepository<ReviewLike, Integer> {
    boolean existsByBookReview_ReviewIdAndProfile_ProfileId(Integer reviewId, Long profileId);

    void deleteByProfile_ProfileIdAndBookReview_ReviewId(Long profileId, Integer reviewId);

    @Query("SELECT COUNT(l) FROM ReviewLike l WHERE l.bookReview.reviewId = :reviewId")
    Integer countLikesByReviewId(@Param("reviewId") Integer reviewId);
}