package com.team.teamreadioserver.bookReview.service;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.BookReviewDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.entity.ReviewLike;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.bookReview.repository.LikesRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.report.entity.ReportedReview;
import com.team.teamreadioserver.report.repository.ReportedReviewRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional; // Optional import

@Service
public class BookReviewService {
    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ReportedReviewRepository reportedReviewRepository;
    @Autowired
    private ProfileRepository profileRepository;

    // 리뷰 등록
    // 로그인된 사용자의 userId를 받아 해당 Profile과 연결합니다.
    @Transactional // 리뷰 등록도 트랜잭션으로 묶는 것이 좋습니다.
    public void addBookReview(ReviewRequestDTO reviewRequestDTO, String userId) {
        // userId를 통해 Profile 엔티티를 조회합니다.
        // ProfileRepository에 findByUser_UserId(String userId) 메서드가 필요합니다.
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));

        BookReview bookReview = BookReview.builder()
                .profile(profile) // 조회한 Profile 엔티티를 연결
                .bookIsbn(reviewRequestDTO.getBookIsbn())
                .reviewContent(reviewRequestDTO.getReviewContent())
                .reportedCount(0) // @PrePersist에서 기본값 0으로 설정되므로 여기서는 명시할 필요 없음
                .build();
        bookReviewRepository.save(bookReview);
    }

    // 신고
    @Transactional
    public void reportReview(Integer reviewId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        bookReview.report(); // 신고 카운트 증가

        if (bookReview.getReportedCount() == 1) {
            // BookReview 엔티티에 직접 연결된 Profile 객체를 사용
            Profile profile = bookReview.getProfile();
            // ReportedReview 생성 시 userId는 profile.getUser().getUserId()로 가져옴
            ReportedReview reportedReview = new ReportedReview(bookReview.getReviewId(), profile.getUser().getUserId());
            reportedReviewRepository.save(reportedReview);
        } else if (bookReview.getReportedCount() > 4) {
            bookReview.hide(); // 5회 이상 신고 시 숨김 처리
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Integer reviewId) {
        // 삭제 전 리뷰 존재 여부 확인 (선택 사항이지만 안전성 증가)
        if (!bookReviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("삭제하려는 리뷰가 존재하지 않습니다: " + reviewId);
        }
        bookReviewRepository.deleteById(reviewId);
    }

    // 리뷰 전체 조회
    public List<AllReviewResponseDTO> allBookReview() {
        return bookReviewRepository.findAll().stream().map(review ->
                AllReviewResponseDTO.builder()
                        .reviewId(review.getReviewId())
                        .penName(review.getProfile().getPenName()) // Profile 엔티티에서 필명(penName) 가져오기
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build()
        ).toList();
    }

    // 내 리뷰 (피드)
    // 현재 로그인된 사용자의 profileId를 받아 자신의 리뷰를 조회합니다.
    public List<MyReviewResponseDTO> myBookReview(Long profileId) { // profileId를 Long 타입으로 받음
        // Profile 엔티티의 profileId를 기준으로 조회 (Repository에 findByProfile_ProfileId 메서드 필요)
        return bookReviewRepository.findByProfile_ProfileId(profileId).stream().map(review ->
                MyReviewResponseDTO.builder()
                        .reviewId(review.getReviewId())
                        .bookIsbn(review.getBookIsbn())
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build()
        ).toList();
    }

    // 리뷰 좋아요 등록
    @Transactional
    public void addLikeBookReview(Integer reviewId, Long profileId) {
        // likesRepository에 existsByBookReview_ReviewIdAndProfile_ProfileId(Integer, Long) 메서드 필요
        boolean alreadyLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        if (alreadyLiked) {
            throw new IllegalArgumentException("이미 좋아요한 리뷰입니다!");
        }

        // ReviewLike 엔티티의 @ManyToOne 관계를 통해 엔티티 객체를 직접 넣어주는 것이 더 정확합니다.
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다. (ProfileId: " + profileId + ")"));
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")"));

        ReviewLike reviewLike = ReviewLike.builder()
                .profile(profile)
                .bookReview(bookReview)
                .build();
        likesRepository.save(reviewLike);
    }

    // 리뷰 좋아요 삭제
    @Transactional
    public void removeLikeBookReview(Integer reviewId, Long profileId) {
        // likesRepository에 deleteByProfile_ProfileIdAndBookReview_ReviewId(Long, Integer) 메서드 필요
        likesRepository.deleteByProfile_ProfileIdAndBookReview_ReviewId(profileId, reviewId);
    }

    // 리뷰 좋아요 카운트
    public Integer getLikesCount(Integer reviewId) {
        return likesRepository.countLikesByReviewId(reviewId);
    }

    // 책별 리뷰 조회
    public List<BookReviewDTO> getBookReviewByBookIsbn(String bookIsbn) {
        List<BookReview> foundBookReviews = bookReviewRepository.findByBookIsbn(bookIsbn);
        return foundBookReviews.stream().map(review -> {
            BookReviewDTO dto = modelMapper.map(review, BookReviewDTO.class);
            dto.setProfileId(review.getProfile().getProfileId());
            dto.setPenName(review.getProfile().getPenName()); // <-- 이 라인을 추가합니다.
            return dto;
        }).collect(Collectors.toList());
    }

    public Profile getProfileByUserId(String userId) {
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));
    }
}