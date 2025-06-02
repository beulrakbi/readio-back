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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // ✨ 수정: ReportedReview 엔티티가 BookReview 객체를 참조하도록 변경 ✨
        // reported_review 테이블에 이미 해당 review_id로 신고된 기록이 있는지 확인하는 로직 필요
        // 중복 신고를 방지하고 싶다면, reportedReviewRepository에 existsByBookReview_ReviewIdAndUserId 같은 메서드를 추가하여 확인해야 합니다.
        // 현재는 첫 신고 시 ReportedReview를 저장하고, 이후 신고는 reportedCount만 증가시키는 로직이므로,
        // 아래 로직은 첫 신고 시에만 ReportedReview를 생성하도록 유지합니다.

        // 만약 ReportedReview가 이미 존재하고, 중복 신고를 또 기록하고 싶지 않다면 아래 로직을 보완해야 합니다.
        // 여기서는 reviewId와 신고자의 userId로 ReportedReview를 찾는 방식으로 변경합니다.

        // 현재 로직은 reportedCount == 1 일 때만 ReportedReview를 생성하므로,
        // 첫 신고 시 ReportedReview를 생성하고, 이후에는 count만 올리므로
        // ReportedReview 레코드는 한 리뷰당 하나만 존재할 가능성이 높습니다.
        // 이 경우, reportedReviewRepository.findByBookReview_ReviewId(reviewId) 등으로 기존 신고 기록을 찾아
        // 업데이트하는 방식이 더 적절할 수 있습니다.

        // 단순하게 최초 신고 시 ReportedReview를 저장하는 현재 로직을 유지하면서
        // ReportedReview 엔티티의 변경에 맞춰 수정합니다.
        if (bookReview.getReportedCount() == 1) {
            // ReportedReview가 BookReview 객체를 참조하도록 변경되었으므로
            // reviewId 대신 bookReview 객체 자체를 전달해야 합니다.
            ReportedReview reportedReview = ReportedReview.builder()
                    .bookReview(bookReview) // BookReview 객체 직접 주입
                    .userId(bookReview.getProfile().getUser().getUserId()) // 신고한 사용자 ID (리뷰 작성자가 신고한 경우)
                    // 만약 다른 사용자가 신고하는 경우, getCurrentUserId() 등으로 가져와야 함.
                    // 현재 로직상 '누가 신고했는지' 정보가 없으므로 이 부분은 확인이 필요합니다.
                    // 일반적으로 신고는 다른 유저가 하므로, userId는 신고한 유저의 ID가 되어야 합니다.
                    .build();
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

    /**
     * 특정 리뷰에 대한 좋아요를 토글합니다.
     * 이미 좋아요를 눌렀으면 좋아요를 취소하고, 누르지 않았으면 좋아요를 추가합니다.
     * @param reviewId 좋아요를 토글할 리뷰 ID
     * @param profileId 현재 사용자 프로필 ID
     * @return 좋아요 추가 또는 삭제 여부 (true: 추가, false: 삭제)
     */
    @Transactional
    public boolean toggleLikeBookReview(Integer reviewId, Long profileId) {
        // 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);

        if (alreadyLiked) {
            // 이미 좋아요를 눌렀으면 좋아요 취소
            likesRepository.deleteByProfile_ProfileIdAndBookReview_ReviewId(profileId, reviewId);
            return false; // 좋아요 삭제
        } else {
            // 좋아요를 누르지 않았으면 좋아요 추가
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다. (ProfileId: " + profileId + ")"));
            BookReview bookReview = bookReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. (ReviewId: " + reviewId + ")"));

            ReviewLike reviewLike = ReviewLike.builder()
                    .profile(profile)
                    .bookReview(bookReview)
                    .build();
            likesRepository.save(reviewLike);
            return true; // 좋아요 추가
        }
    }

    // 기존 좋아요 등록/삭제 메서드는 사용하지 않거나, 위 toggle 메서드로 대체하는 것을 고려할 수 있습니다.
    // 현재 프론트엔드에서 POST/DELETE를 명시적으로 보내고 있으므로, 백엔드도 그에 맞게 유지하는 것이 좋습니다.
    // 하지만 "좋아요를 누르면 카운트가 1 올라가고 다시 누르면 1이 0으로 내려가야 한다"는 요구사항을 충족시키려면
    // 프론트에서 isLiked 상태를 정확히 인지하고 요청을 보내야 합니다.
    // 아래 두 메서드는 그대로 두더라도, 실제 좋아요 로직은 `toggleLikeBookReview`처럼 동작해야 합니다.

    // 리뷰 좋아요 등록 (기존)
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

    // 리뷰 좋아요 삭제 (기존)
    @Transactional
    public void removeLikeBookReview(Integer reviewId, Long profileId) {
        // 존재하지 않는 좋아요를 삭제하려고 시도하는 경우 예외 처리 추가
        boolean exists = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        if (!exists) {
            throw new IllegalArgumentException("해당 리뷰에 좋아요를 누르지 않았습니다.");
        }
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

        return foundBookReviews.stream().map(review -> {
            BookReviewDTO dto = new BookReviewDTO();

            dto.setReviewId(review.getReviewId());
            dto.setProfileId(review.getProfile().getProfileId());
            dto.setPenName(review.getProfile().getPenName());
            // reviewerUserId를 설정하는 부분에서 NPE 발생 가능성이 있으므로 Optional 처리
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
        List<BookReviewDTO> result = new ArrayList<>();
        for (BookReview foundBookReview : foundBookReviews)
        {
            Profile profile = profileRepository.findByProfileId(foundBookReview.getProfileId());
            BookReviewDTO bookReviewDTO = new BookReviewDTO(foundBookReview.getReviewId(), foundBookReview.getProfileId(), foundBookReview.getBookIsbn(),
                    foundBookReview.getReviewContent(), foundBookReview.getReportedCount(), foundBookReview.getIsHidden(), foundBookReview.getCreatedAt(), profile.getPenName());

            result.add(bookReviewDTO);
        }

        return result;
    }

    public Profile getProfileByUserId(String userId) {
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));
    }
}