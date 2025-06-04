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
import jakarta.persistence.EntityNotFoundException; // jakarta.persistence import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Optional; // Optional import 확인
import org.slf4j.Logger; // SLF4J Logger import (권장)
import org.slf4j.LoggerFactory; // SLF4J LoggerFactory import (권장)

@Service
public class BookReviewService {
    // SLF4J Logger 선언 (권장)
    private static final Logger logger = LoggerFactory.getLogger(BookReviewService.class);

    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ReportedReviewRepository reportedReviewRepository;
    @Autowired
    private ProfileRepository profileRepository;

    // --- 기존 코드 유지 (addBookReview, reportReview, deleteReview, allBookReview, myBookReview) ---
    public void addBookReview(ReviewRequestDTO reviewRequestDTO, String userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));

        BookReview bookReview = BookReview.builder()
                .profile(profile)
                .bookIsbn(reviewRequestDTO.getBookIsbn())
                .reviewContent(reviewRequestDTO.getReviewContent())
                .reportedCount(0)
                .build();
        bookReviewRepository.save(bookReview);
    }

    @Transactional
    public void reportReview(Integer reviewId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰가 존재하지 않습니다."));

        bookReview.report();

        if (bookReview.getReportedCount() == 1) {
            ReportedReview reportedReview = ReportedReview.builder()
                    .bookReview(bookReview)
                    .userId(bookReview.getProfile().getUser().getUserId())
                    .build();
            reportedReviewRepository.save(reportedReview);
        } else if (bookReview.getReportedCount() > 4) {
            bookReview.hide2();
        }
    }

    @Transactional
    public void deleteReview(Integer reviewId, String currentUserId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("삭제하려는 리뷰가 존재하지 않습니다: " + reviewId));

        Profile currentUserProfile = getProfileByUserId(currentUserId);

        if (!bookReview.getProfile().getProfileId().equals(currentUserProfile.getProfileId())) {
            throw new AccessDeniedException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        bookReviewRepository.deleteById(reviewId);
    }

    public List<AllReviewResponseDTO> allBookReview() {
        return bookReviewRepository.findAll().stream().map(review ->
                AllReviewResponseDTO.builder()
                        .reviewId(review.getReviewId())
                        .penName(review.getProfile().getPenName())
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build()
        ).toList();
    }

    public List<MyReviewResponseDTO> myBookReview(Long profileId) {
        return bookReviewRepository.findByProfile_ProfileId(profileId).stream().map(review ->
                MyReviewResponseDTO.builder()
                        .reviewId(review.getReviewId())
                        .bookIsbn(review.getBookIsbn())
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .build()
        ).toList();
    }

    // 책별 리뷰 조회 (기존과 동일, DTO에 좋아요 수와 좋아요 여부 잘 설정됨)
    public List<BookReviewDTO> getBookReviewByBookIsbn(String bookIsbn) {
        logger.info("[SERVICE] getBookReviewByBookIsbn 호출: bookIsbn={}", bookIsbn);
        List<BookReview> foundBookReviews = bookReviewRepository.findByBookIsbn(bookIsbn);
        String currentUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserId = authentication.getName();
        }
        logger.info("[SERVICE] 현재 사용자 ID (from SecurityContext): {}", currentUserId);

        final String finalCurrentUserId = currentUserId;

        return foundBookReviews.stream().map(review -> {
            BookReviewDTO dto = new BookReviewDTO();

            dto.setReviewId(review.getReviewId());
            dto.setProfileId(review.getProfile().getProfileId());
            dto.setPenName(review.getProfile().getPenName());
            dto.setReviewerUserId(review.getProfile().getUser() != null ? review.getProfile().getUser().getUserId() : null);
            dto.setReviewContent(review.getReviewContent());
            dto.setBookIsbn(review.getBookIsbn());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setIsHidden(review.getIsHidden());
            dto.setReportedCount(review.getReportedCount());

            Integer likesCount = likesRepository.countLikesByReviewId(review.getReviewId());
            dto.setLikesCount(likesCount != null ? likesCount : 0);

            boolean calculatedIsLiked = false;
            if (finalCurrentUserId != null) {
                Optional<Profile> currentUserProfileOpt = profileRepository.findByUser_UserId(finalCurrentUserId);
                if (currentUserProfileOpt.isPresent()) {
                    Long currentUserProfileId = currentUserProfileOpt.get().getProfileId();
                    logger.info("[SERVICE] 리뷰 ID: {}, 현재 사용자 프로필 ID: {} 로 좋아요 여부 확인 시도", review.getReviewId(), currentUserProfileId);
                    calculatedIsLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(review.getReviewId(), currentUserProfileId);
                } else {
                    logger.warn("[SERVICE] 사용자 ID {} 에 해당하는 프로필을 찾을 수 없습니다.", finalCurrentUserId);
                }
            }
            dto.setLiked(calculatedIsLiked); // Lombok @Setter에 의해 setLiked(boolean) 호출

            // System.out.println 사용 시 (기존 코드 유지 또는 아래 logger로 대체)
            // System.out.println("DEBUG_BE: Review ID " + review.getReviewId() +
            //         " - IsLiked calculated: " + calculatedIsLiked +
            //         " (Current User: " + (finalCurrentUserId != null ? finalCurrentUserId : "null") + ")");
            logger.info("[SERVICE-DEBUG_BE] Review ID: {}, IsLiked calculated: {}, LikesCount: {}, Current User: {}",
                    review.getReviewId(), calculatedIsLiked, dto.getLikesCount(), (finalCurrentUserId != null ? finalCurrentUserId : "비로그인 또는 식별불가"));

            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public boolean toggleLikeBookReview(Integer reviewId, Long profileId) {
        logger.info("[SERVICE] toggleLikeBookReview 호출: reviewId={}, profileId={}", reviewId, profileId);

        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("[SERVICE] 리뷰를 찾을 수 없음: reviewId={}", reviewId);
                    return new EntityNotFoundException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")");
                });

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> {
                    logger.error("[SERVICE] 프로필을 찾을 수 없음: profileId={}", profileId);
                    return new EntityNotFoundException("프로필을 찾을 수 없습니다. (ProfileId: " + profileId + ")");
                });

        boolean alreadyLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        logger.info("[SERVICE] reviewId: {}, profileId: {} - Already Liked: {}", reviewId, profileId, alreadyLiked);

        if (alreadyLiked) {
            likesRepository.deleteByProfile_ProfileIdAndBookReview_ReviewId(profileId, reviewId);
            logger.info("[SERVICE] reviewId: {} 좋아요 취소됨.", reviewId);
            return false; // 좋아요 삭제됨
        } else {
            ReviewLike reviewLike = ReviewLike.builder()
                    .profile(profile)
                    .bookReview(bookReview)
                    .build();
            likesRepository.save(reviewLike);
            logger.info("[SERVICE] reviewId: {} 좋아요 추가됨.", reviewId);
            return true; // 좋아요 추가됨
        }
    }

    @Transactional(readOnly = true)
    public Integer getLikesCount(Integer reviewId) {
        logger.info("[SERVICE] getLikesCount 호출: reviewId={}", reviewId);
        bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("[SERVICE] getLikesCount: 리뷰를 찾을 수 없음: reviewId={}", reviewId);
                    return new EntityNotFoundException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")");
                });
        Integer count = likesRepository.countLikesByReviewId(reviewId);
        logger.info("[SERVICE] reviewId: {} 의 좋아요 수: {}", reviewId, count);
        return count;
    }

    @Transactional(readOnly = true)
    public boolean isReviewLikedByUser(Integer reviewId, Long profileId) {
        logger.info("[SERVICE] isReviewLikedByUser 호출: reviewId={}, profileId={}", reviewId, profileId);
        boolean liked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        logger.info("[SERVICE] reviewId: {}, profileId: {} - Liked by user: {}", reviewId, profileId, liked);
        return liked;
    }

    public Profile getProfileByUserId(String userId) {
        logger.info("[SERVICE] getProfileByUserId 호출: userId={}", userId);
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("[SERVICE] getProfileByUserId: 사용자 프로필을 찾을 수 없음: userId={}", userId);
                    return new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")");
                });
    }
}