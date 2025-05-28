package com.team.teamreadioserver.bookReview.service;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.entity.ReviewLike;
import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.bookReview.repository.LikesRepository;
import com.team.teamreadioserver.search.dto.BookRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookReviewService {
    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;

    //리뷰 등록
    public void addBookReview(ReviewRequestDTO reviewRequestDTO) {
        BookReview bookReview = BookReview.builder()
                .bookIsbn(reviewRequestDTO.getBookIsbn())
                .reviewContent(reviewRequestDTO.getReviewContent())
                .reportedCount(0)
                .build();
        bookReviewRepository.save(bookReview);
    }

    //신고
    public void reportReview(Integer reviewId){
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        bookReview.report();
        bookReviewRepository.save(bookReview);
    }

    //리뷰 삭제
    public void deleteReview(Integer reviewId){
        bookReviewRepository.deleteById(reviewId);
    }

    //리뷰 전체 조회
    public List<AllReviewResponseDTO> allBookReview() {
        return bookReviewRepository.findAll().stream()
                .map(review -> AllReviewResponseDTO.builder()
                        .profileId(review.getProfileId())
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }


    //내 리뷰 (피드)
    public List<MyReviewResponseDTO> myBookReview(Integer profileId){
        return bookReviewRepository.findByProfileId(profileId).stream()
                .map(review -> MyReviewResponseDTO.builder()
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }


    //리뷰 좋아요 등록
    public void addLikeBookReview(Integer reviewId, Integer profileId){
        boolean alreadyLiked = likesRepository.existsByReviewIdAndProfileId(reviewId, profileId);
        if(alreadyLiked){
            throw new IllegalArgumentException("이미 좋아요한 리뷰입니다!");
        }

        ReviewLike reviewLike = ReviewLike.builder()
                .reviewId(reviewId)
                .profileId(profileId)
                .build();
        likesRepository.save(reviewLike);
    }

    //리뷰 좋아요 삭제
    @Transactional
    public void removeLikeBookReview(Integer reviewId, Integer profileId){
        likesRepository.deleteByProfileIdAndReviewId(profileId, reviewId);
    }

    //리뷰 좋아요 카운트
    public Integer getLikesCount(Integer reviewId){
        return likesRepository.countLikesByReviewId(reviewId);
    }

}
