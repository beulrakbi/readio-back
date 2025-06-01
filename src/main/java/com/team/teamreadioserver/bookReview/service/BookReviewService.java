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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class BookReviewService {
    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ModelMapper modelMapper; // ModelMapper는 이제 수동 매핑으로 인해 사용되지 않을 수 있습니다.
    @Autowired
    private ReportedReviewRepository reportedReviewRepository;
    @Autowired
    private ProfileRepository profileRepository;

    // 리뷰 등록
    @Transactional
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

    // 신고
    @Transactional
    public void reportReview(Integer reviewId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        bookReview.report();

        if (bookReview.getReportedCount() == 1) {
            Profile profile = bookReview.getProfile();
            ReportedReview reportedReview = new ReportedReview(bookReview.getReviewId(), profile.getUser().getUserId());
            reportedReviewRepository.save(reportedReview);
        } else if (bookReview.getReportedCount() > 4) {
            bookReview.hide();
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Integer reviewId, String currentUserId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 리뷰가 존재하지 않습니다: " + reviewId));

        Profile currentUserProfile = getProfileByUserId(currentUserId);

        if (!bookReview.getProfile().getProfileId().equals(currentUserProfile.getProfileId())) {
            throw new AccessDeniedException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        bookReviewRepository.deleteById(reviewId);
    }

    // 리뷰 전체 조회
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

    // 내 리뷰 (피드)
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

    // 리뷰 좋아요 등록
    @Transactional
    public void addLikeBookReview(Integer reviewId, Long profileId) {
        boolean alreadyLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        if (alreadyLiked) {
            throw new IllegalArgumentException("이미 좋아요한 리뷰입니다!");
        }

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
        likesRepository.deleteByProfile_ProfileIdAndBookReview_ReviewId(profileId, reviewId);
    }

    // 리뷰 좋아요 카운트
    public Integer getLikesCount(Integer reviewId) {
        return likesRepository.countLikesByReviewId(reviewId);
    }

    // 책별 리뷰 조회
    public List<BookReviewDTO> getBookReviewByBookIsbn(String bookIsbn) {
        List<BookReview> foundBookReviews = bookReviewRepository.findByBookIsbn(bookIsbn);

        String currentUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserId = authentication.getName();
        }

        final String finalCurrentUserId = currentUserId;

        // --- ✨ 이 return foundBookReviews.stream().map(review -> { ... }) 블록 전체를 아래 코드로 교체합니다. ✨ ---
        return foundBookReviews.stream().map(review -> {
            // ModelMapper를 주석 처리하고 DTO를 수동으로 생성하여 명시적으로 필드 설정합니다.
            BookReviewDTO dto = new BookReviewDTO();

            dto.setReviewId(review.getReviewId());
            dto.setProfileId(review.getProfile().getProfileId());
            dto.setPenName(review.getProfile().getPenName());
            dto.setReviewerUserId(review.getProfile().getUser().getUserId());
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
                    calculatedIsLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(review.getReviewId(), currentUserProfileId);
                }
            }
            dto.setLiked(calculatedIsLiked); // 최종 계산된 isLiked 값을 DTO에 설정

            // ✨ System.out.println은 이 위치에 있어야 합니다. ✨
            System.out.println("DEBUG_BE: Review ID " + review.getReviewId() +
                    " - IsLiked calculated: " + calculatedIsLiked +
                    " (Current User: " + finalCurrentUserId + ")");

            return dto; // DTO 객체를 반환합니다.
        }).collect(Collectors.toList());
        // --- ✨ -------------------------------------------------------------------------------------------------- ✨ ---
    }

    public Profile getProfileByUserId(String userId) {
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));
    }
}