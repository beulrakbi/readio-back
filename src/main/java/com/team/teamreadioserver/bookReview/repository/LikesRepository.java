package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<ReviewLike, Integer> {
    // 특정 리뷰에 특정 프로필이 좋아요를 눌렀는지 확인
    boolean existsByBookReview_ReviewIdAndProfile_ProfileId(Integer reviewId, Long profileId);

    // 특정 프로필이 특정 리뷰에 누른 좋아요 기록 삭제
    void deleteByProfile_ProfileIdAndBookReview_ReviewId(Long profileId, Integer reviewId);

    // 특정 리뷰의 좋아요 수 카운트
    @Query("SELECT COUNT(l) FROM ReviewLike l WHERE l.bookReview.reviewId = :reviewId")
    Integer countLikesByReviewId(@Param("reviewId") Integer reviewId);
}