package com.team.teamreadioserver.bookReview.service;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.BookReviewDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.entity.ReviewLike;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.bookReview.repository.LikesRepository;
import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.report.service.ReportedService; // ⭐ ReportedService 임포트

// [확인!] BookService 및 BookDTO 임포트 (실제 프로젝트의 패키지 경로를 확인해주세요)
import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import com.team.teamreadioserver.search.service.BookService; // (여러분이 구현해야 할 BookService 인터페이스)
import com.team.teamreadioserver.search.dto.BookDTO;      // (제공해주신 BookDTO)

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookReviewService {
    private static final Logger logger = LoggerFactory.getLogger(BookReviewService.class);

    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ReportedService reportedService; // ⭐ ReportedService 주입

    // [필수] BookService 주입 (BookService 인터페이스의 구현체가 Spring Bean으로 등록되어 있어야 합니다)
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    // 리뷰 등록
    public void addBookReview(ReviewRequestDTO reviewRequestDTO, String userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")"));

        BookReview bookReview = BookReview.builder()
                .profile(profile)
                .bookIsbn(reviewRequestDTO.getBookIsbn())
                .reviewContent(reviewRequestDTO.getReviewContent())
                .reportedCount(0) // 초기 신고 횟수는 0
                .build();
        bookReviewRepository.save(bookReview);
    }

    // 리뷰 신고 (ReportedService로 로직 위임)
    @Transactional
    public void reportReview(Integer reviewId) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인 사용자 ID
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인된 사용자 정보를 찾을 수 없습니다."); // 로그인되지 않은 경우 예외 처리
        }
        // 신고 로직을 ReportedService로 위임
        reportedService.reportReview(reviewId, currentUserId);
    }

    // 리뷰 삭제 (신고 기록 삭제 로직도 ReportedService로 위임)
    @Transactional
    public void deleteReview(Integer reviewId, String currentUserId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("삭제하려는 리뷰가 존재하지 않습니다: " + reviewId));

        Profile currentUserProfile = getProfileByUserId(currentUserId);

        if (!bookReview.getProfile().getProfileId().equals(currentUserProfile.getProfileId())) {
            throw new AccessDeniedException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        // 해당 리뷰에 대한 좋아요 기록도 삭제
        likesRepository.deleteByBookReview_ReviewId(reviewId);

        // ⭐ 해당 리뷰에 대한 신고 기록도 삭제 (ReportedService로 위임)
        reportedService.deleteReportedReviewsByBookReviewId(reviewId);

        bookReviewRepository.delete(bookReview);
        logger.info("리뷰 삭제 완료 및 관련 좋아요/신고 기록 삭제: reviewId={}", reviewId);
    }

    // 모든 리뷰 조회
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

    public int myBookReviewCounts(String userId)
    {
        return bookReviewRepository.countByProfile(profileRepository.findByUser_UserId(userId).get());
    }

    public List<MyReviewResponseDTO> myReviewCounts(String userId) {
        Optional<Profile> profile = profileRepository.findByUser_UserId(userId);
        List<BookReview> reviews = bookReviewRepository.findByProfile_ProfileId(profile.get().getProfileId());
        List<MyReviewResponseDTO> result = new ArrayList<>();
        for (BookReview review : reviews) {
            MyReviewResponseDTO myReviewResponseDTO = new MyReviewResponseDTO();
            result.add(myReviewResponseDTO);
        }
        return result;
    }

    // --- [수정된 메소드] myBookReview ---
    public List<MyReviewResponseDTO> myBookReview(String userId, Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("reviewId").descending());

        Optional<Profile> profile = profileRepository.findByUser_UserId(userId);
        Page<BookReview> pageReviews = bookReviewRepository.findAllByProfile(profile.get(), paging);
        List<BookReview> reviews = pageReviews.getContent();
        List<MyReviewResponseDTO> result = new ArrayList<>();
        for (BookReview review : reviews) {
            MyReviewResponseDTO myReviewResponseDTO = new MyReviewResponseDTO();
            Book book = bookRepository.findByBookIsbn(review.getBookIsbn());
            if (book != null)
            {
                BookDTO bookDTO = new BookDTO(book);
                myReviewResponseDTO.setBook(bookDTO);
            }
            int likes = likesRepository.countLikesByReviewId(review.getReviewId());
            myReviewResponseDTO.setReviewId(review.getReviewId());
            myReviewResponseDTO.setReviewContent(review.getReviewContent());
            myReviewResponseDTO.setCreatedAt(review.getCreatedAt());
            myReviewResponseDTO.setBookIsbn(review.getBookIsbn());
            myReviewResponseDTO.setLikes(likes);

            result.add(myReviewResponseDTO);
        }

        return result;

//        return reviews.stream().map(review -> {
//            BookDTO bookDetails = null; // BookDTO (from search.dto) 타입으로 변경
//            String isbn = review.getBookIsbn();
//
//            if (isbn != null && !isbn.isEmpty()) {
//                try {
//                    // [필수 구현] BookService를 통해 ISBN으로 책 상세 정보를 가져옵니다.
//                    // bookService.getBookDetailsByIsbn(isbn) 메소드는 BookService 인터페이스와 그 구현체에 직접 만드셔야 합니다.
//                    bookDetails = bookService.getBookDetailsByIsbn(isbn);
//                } catch (Exception e) {
//                    logger.error("ISBN {} 에 대한 책 상세 정보를 가져오는 중 오류 발생: {}", isbn, e.getMessage());
//                    // 책 정보를 가져오지 못한 경우, bookDetails는 null로 유지되거나,
//                    // 기본 정보를 가진 BookDTO를 생성하여 할당할 수 있습니다.
//                    // 예: bookDetails = BookDTO.builder().bookTitle("정보 없음").bookAuthor("정보 없음").bookCover(null).build();
//                }
//            }
//
//            return MyReviewResponseDTO.builder()
//                    .reviewId(review.getReviewId())
//                    .bookIsbn(isbn)
//                    .reviewContent(review.getReviewContent())
//                    .createdAt(review.getCreatedAt())
//                    .book(bookDetails) // MyReviewResponseDTO에 추가된 book 필드에 조회한 BookDTO 설정
//                    .build();
//        }).collect(Collectors.toList());
    }

    // ISBN으로 책 리뷰 조회
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
            // BookReview 엔티티의 isHidden 필드(IsHidden enum 타입)를 DTO의 적절한 타입으로 변환 필요할 수 있음
            // 여기서는 BookReviewDTO에 isHidden 필드가 있고, 타입이 호환된다고 가정합니다.
            dto.setIsHidden(review.getIsHidden());
            dto.setReportedCount(review.getReportedCount()); // ReportedCount는 BookReview 엔티티에 직접 존재한다고 가정

            Integer likesCount = likesRepository.countLikesByReviewId(review.getReviewId());
            dto.setLikesCount(likesCount != null ? likesCount : 0);

            boolean calculatedIsLiked = false;
            if (finalCurrentUserId != null) {
                Optional<Profile> currentUserProfileOpt = profileRepository.findByUser_UserId(finalCurrentUserId);
                if (currentUserProfileOpt.isPresent()) {
                    Long currentUserProfileId = currentUserProfileOpt.get().getProfileId();
                    // logger.info("[SERVICE] 리뷰 ID: {}, 현재 사용자 프로필 ID: {} 로 좋아요 여부 확인 시도", review.getReviewId(), currentUserProfileId);
                    calculatedIsLiked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(review.getReviewId(), currentUserProfileId);
                } else {
                    logger.warn("[SERVICE] 사용자 ID {} 에 해당하는 프로필을 찾을 수 없습니다.", finalCurrentUserId);
                }
            }
            dto.setLiked(calculatedIsLiked);

            logger.info("[SERVICE-DEBUG_BE] Review ID: {}, IsLiked calculated: {}, LikesCount: {}, Current User: {}",
                    review.getReviewId(), calculatedIsLiked, dto.getLikesCount(), (finalCurrentUserId != null ? finalCurrentUserId : "비로그인 또는 식별불가"));

            return dto;
        }).collect(Collectors.toList());
    }

    // 리뷰 좋아요 토글
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

    // 리뷰 좋아요 수 조회
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

    // 사용자가 리뷰에 좋아요를 눌렀는지 확인
    @Transactional(readOnly = true)
    public boolean isReviewLikedByUser(Integer reviewId, Long profileId) {
        logger.info("[SERVICE] isReviewLikedByUser 호출: reviewId={}, profileId={}", reviewId, profileId);
        boolean liked = likesRepository.existsByBookReview_ReviewIdAndProfile_ProfileId(reviewId, profileId);
        logger.info("[SERVICE] reviewId: {}, profileId: {} - Liked by user: {}", reviewId, profileId, liked);
        return liked;
    }

    // 사용자 ID로 프로필 조회
    public Profile getProfileByUserId(String userId) {
        logger.info("[SERVICE] getProfileByUserId 호출: userId={}", userId);
        return profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("[SERVICE] getProfileByUserId: 사용자 프로필을 찾을 수 없음: userId={}", userId);
                    return new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다. (UserId: " + userId + ")");
                });
    }
}