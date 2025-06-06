package com.team.teamreadioserver.bookReview.controller;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.BookReviewDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.service.BookReviewService;
import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class BookReviewController {

    private static final Logger logger = LoggerFactory.getLogger(BookReviewController.class); // SLF4J Logger

    @Autowired
    private BookReviewService bookReviewService;

    // --- 기존 코드 유지 (createBookReview, report, deleteReview, getAllBookReviews, getAllReviews, getMyReviews) ---
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @PostMapping("/bookReview/create")
    public ResponseEntity<String> createBookReview(@RequestBody @Valid ReviewRequestDTO reviewRequestDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            bookReviewService.addBookReview(reviewRequestDTO, userId);
            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "리뷰 신고 등록", description = "리뷰를 신고합니다.")
    @PutMapping("/bookReview/{reviewId}/report")
    public ResponseEntity<?> report(@PathVariable Integer reviewId) {
        try {
            bookReviewService.reportReview(reviewId);
            return ResponseEntity.ok().body(Map.of("message", "리뷰가 성공적으로 신고되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "리뷰 신고 중 오류 발생: " + e.getMessage()));
        }
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @DeleteMapping("/bookReview/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            bookReviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok("리뷰가 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "책 별 리뷰 조회", description = "책 별 리뷰를 조회합니다.")
    @GetMapping("/bookReview/{bookIsbn}")
    public ResponseEntity<ResponseDTO> getAllBookReviews(@PathVariable String bookIsbn) {
        logger.info("[CONTROLLER] getAllBookReviews 호출: bookIsbn={}", bookIsbn);
        List<BookReviewDTO> reviews = bookReviewService.getBookReviewByBookIsbn(bookIsbn);
        System.out.println("reviews: " + reviews);
        logger.info("[CONTROLLER] getAllBookReviews 응답 데이터 (일부): 첫 번째 리뷰의 isLiked={}, likesCount={}", reviews.isEmpty() ? "N/A" : reviews.get(0).isLiked(), reviews.isEmpty() ? "N/A" : reviews.get(0).getLikesCount());
        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "리뷰 조회 성공", reviews));
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<List<AllReviewResponseDTO>> getAllReviews() {
        List<AllReviewResponseDTO> reviews = bookReviewService.allBookReview();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/mylibrary/reviews")
    public ResponseEntity<?> getMyReviews(@RequestParam(name = "offset", defaultValue = "1") String offset) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        int total = bookReviewService.myBookReviewCounts(userId);
        Criteria cri = new Criteria(Integer.valueOf(offset), 3);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();
        pagingResponseDTO.setData(bookReviewService.myBookReview(userId, cri));
        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 리뷰 전체 조회 성공", pagingResponseDTO));
    }

    @Operation(summary = "내 리뷰 개수 조회", description = "특정 사용자의 리뷰 개수를 조회합니다.")
    @GetMapping("/mylibrary/reviews/count")
    public ResponseEntity<?> getMyReviewsCount() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        List<MyReviewResponseDTO> result = bookReviewService.myReviewCounts(userId);
        System.out.println("개수: " + result.size());

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 리뷰 개수 조회 성공", result));
    }


    @Operation(summary = "리뷰 좋아요/취소 토글", description = "리뷰에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/bookReview/{reviewId}/like-toggle")
    public ResponseEntity<?> toggleLikeReview(@PathVariable Integer reviewId) {
        logger.info("[CONTROLLER] toggleLikeReview 호출: reviewId={}", reviewId);
        String userId = getCurrentUserId();
        if (userId == null) {
            logger.warn("[CONTROLLER] toggleLikeReview: 로그인이 필요합니다 (userId is null).");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            Profile profile = bookReviewService.getProfileByUserId(userId);
            boolean isLiked = bookReviewService.toggleLikeBookReview(reviewId, profile.getProfileId());
            Integer likesCount = bookReviewService.getLikesCount(reviewId);

            String message = isLiked ? "리뷰 좋아요 완료" : "리뷰 좋아요 취소 완료";
            HttpStatus status = isLiked ? HttpStatus.CREATED : HttpStatus.OK;

            Map<String, Object> responseBody = Map.of("message", message, "isLiked", isLiked, "likesCount", likesCount);
            logger.info("[CONTROLLER] toggleLikeReview 응답: {}", responseBody);
            return ResponseEntity.status(status).body(responseBody);
        } catch (EntityNotFoundException e) {
            logger.error("[CONTROLLER] toggleLikeReview EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("[CONTROLLER] toggleLikeReview Exception: {}", e.getMessage(), e); // 스택 트레이스 로깅
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "좋아요 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    // --- (getLikesCount, getReviewLikeStatus, getCurrentUserId 기존 코드 유지) ---
    @Operation(summary = "리뷰 좋아요 수 조회", description = "리뷰의 좋아요 수를 조회합니다.")
    @GetMapping("/bookReview/{reviewId}/likes/count")
    public ResponseEntity<?> getLikesCount(@PathVariable Integer reviewId) {
        try {
            Integer count = bookReviewService.getLikesCount(reviewId);
            return ResponseEntity.ok(Map.of("reviewId", reviewId, "likesCount", count));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "좋아요 수 조회 중 오류 발생: " + e.getMessage()));
        }
    }

    @Operation(summary = "리뷰 좋아요 상태 확인", description = "현재 로그인된 사용자가 특정 리뷰에 좋아요를 눌렀는지 확인합니다.")
    @GetMapping("/bookReview/{reviewId}/likes/status")
    public ResponseEntity<Map<String, Object>> getReviewLikeStatus(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.ok(Map.of("reviewId", reviewId, "isLiked", false));
        }

        try {
            Profile profile = bookReviewService.getProfileByUserId(userId);
            boolean isLiked = bookReviewService.isReviewLikedByUser(reviewId, profile.getProfileId());
            return ResponseEntity.ok(Map.of("reviewId", reviewId, "isLiked", isLiked));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "isLiked", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "좋아요 상태 조회 중 오류 발생: " + e.getMessage(), "isLiked", false));
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        // logger.debug("[CONTROLLER] getCurrentUserId: {}", authentication.getName()); // 필요시 debug 레벨로 사용자 ID 로깅
        return authentication.getName();
    }
}