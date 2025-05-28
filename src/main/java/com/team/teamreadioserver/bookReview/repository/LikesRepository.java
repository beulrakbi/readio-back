package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.entity.ReviewLike;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<ReviewLike, Integer> {
    boolean existsByReviewIdAndProfileId(Integer reviewId, Integer profileId);

    void deleteByProfileIdAndReviewId(Integer profileId, Integer reviewId);

    @Query("SELECT COUNT(l) FROM ReviewLike l WHERE l.reviewId = :reviewId")
    Integer countLikesByReviewId(@Param("reviewId") Integer reviewId);
}
