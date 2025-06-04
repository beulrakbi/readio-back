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

@Service
public class BookReviewService {
    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;
    // @Autowired private ModelMapper modelMapper; // 사용되지 않아 주석 처리
    @Autowired
    private ReportedReviewRepository reportedReviewRepository;
    @Autowired
    private ProfileRepository profileRepository;

    // --- 기존 코드 유지 (좋아요 관련 없는 부분) ---

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

    // 신고 (로직은 원래대로 유지하되, 주석 처리된 부분은 제거)
    @Transactional
    public void reportReview(Integer reviewId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰가 존재하지 않습니다.")); // IllegalArgumentException 대신 EntityNotFoundException 사용

        bookReview.report();

        if (bookReview.getReportedCount() == 1) {
            ReportedReview reportedReview = ReportedReview.builder()
                    .bookReview(bookReview)
                    .userId(bookReview.getProfile().getUser().getUserId()) // 이 userId는 리뷰 작성자의 ID로 보임. 신고한 유저의 ID로 변경 필요 시 수정
                    .build();
            reportedReviewRepository.save(reportedReview);
        } else if (bookReview.getReportedCount() > 4) {
            bookReview.hide2();
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Integer reviewId, String currentUserId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("삭제하려는 리뷰가 존재하지 않습니다: " + reviewId)); // IllegalArgumentException 대신 EntityNotFoundException 사용

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

    // --- 좋아요 관련 메서드 수정 ---

    /**
     * 특정 리뷰에 대한 좋아요를 토글합니다.
     * 이미 좋아요를 눌렀으면 좋아요를 취소하고, 누르지 않았으면 좋아요를 추가합니다.
     * @param reviewId 좋아요를 토글할 리뷰 ID
     * @param profileId 현재 사용자 프로필 ID
     * @return 좋아요 추가 또는 삭제 여부 (true: 좋아요 추가, false: 좋아요 삭제)
     * @throws EntityNotFoundException 리뷰 또는 프로필을 찾을 수 없을 때 발생
     */
    @Transactional
    public boolean toggleLikeBookReview(Integer reviewId, Long profileId) {
        // 1. 리뷰 엔티티 조회
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")"));

        // 2. 프로필 엔티티 조회
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다. (ProfileId: " + profileId + ")"));

        // 3. 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);

        if (alreadyLiked) {
            // 이미 좋아요를 눌렀으면 좋아요 취소
            likesRepository.deleteByProfile_ProfileIdAndBookReview_ReviewId(profileId, reviewId);
            return false; // 좋아요 삭제됨
        } else {
            // 좋아요를 누르지 않았으면 좋아요 추가
            ReviewLike reviewLike = ReviewLike.builder()
                    .profile(profile)
                    .bookReview(bookReview)
                    .build();
            likesRepository.save(reviewLike);
            return true; // 좋아요 추가됨
        }
    }

    // 리뷰 좋아요 수 조회 (기존과 동일)
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 추가
    public Integer getLikesCount(Integer reviewId) {
        // 리뷰가 존재하는지 먼저 확인하는 것이 좋습니다.
        bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")"));
        return likesRepository.countLikesByReviewId(reviewId);
    }

    // 특정 사용자가 특정 리뷰에 좋아요를 눌렀는지 확인하는 메서드 추가
    @Transactional(readOnly = true)
    public boolean isReviewLikedByUser(Integer reviewId, Long profileId) {
        // 리뷰 존재 여부 확인 (선택 사항, 없으면 existsBy에서 false 반환할 것이므로)
        // bookReviewRepository.findById(reviewId)
        //         .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")"));
        return likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
    }

    // 책별 리뷰 조회 (기존과 동일, DTO에 좋아요 수와 좋아요 여부 잘 설정됨)
    public List<BookReviewDTO> getBookReviewByBookIsbn(String bookIsbn) {
        List<BookReview> foundBookReviews = bookReviewRepository.findByBookIsbn(bookIsbn);
        String currentUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserId = authentication.getName();
        }

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
                    calculatedIsLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(review.getReviewId(), currentUserProfileId);
                }
            }
            dto.setLiked(calculatedIsLiked);

            System.out.println("DEBUG_BE: Review ID " + review.getReviewId() +
                    " - IsLiked calculated: " + calculatedIsLiked +
                    " (Current User: " + (finalCurrentUserId != null ? finalCurrentUserId : "null") + ")");

            return dto;
        }).collect(Collectors.toList());
    }

    // 사용자 프로필 조회 메서드 (기존과 동일)
    public Profile getProfileByUserId(String userId) {
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")")); // IllegalArgumentException 대신 EntityNotFoundException 사용
    }
}