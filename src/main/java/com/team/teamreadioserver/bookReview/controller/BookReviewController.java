package com.team.teamreadioserver.bookReview.controller;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.service.BookReviewService;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException; // EntityNotFoundException import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // AccessDeniedException import
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Map을 사용하여 JSON 응답 구성

@RestController
@RequestMapping("/bookReview")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    // --- 기존 코드 유지 (좋아요 관련 없는 부분) ---

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @PostMapping("/create")
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
    @PutMapping("/{reviewId}/report")
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
    @DeleteMapping("/delete/{reviewId}")
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
        } catch (IllegalArgumentException e) { // getProfileByUserId에서 발생 가능
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "책 별 리뷰 조회", description = "책 별 리뷰를 조회합니다.")
    @GetMapping("/{bookIsbn}")
    public ResponseEntity<ResponseDTO> getAllBookReviews(@PathVariable String bookIsbn) {
        // 기존 ResponseDTO 사용
        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "리뷰 조회 성공", bookReviewService.getBookReviewByBookIsbn(bookIsbn)));
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<List<AllReviewResponseDTO>> getAllReviews() {
        List<AllReviewResponseDTO> reviews = bookReviewService.allBookReview();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/reviews/my")
    public ResponseEntity<?> getMyReviews() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            Profile profile = bookReviewService.getProfileByUserId(userId);
            List<MyReviewResponseDTO> myReviews = bookReviewService.myBookReview(profile.getProfileId());
            return ResponseEntity.ok(myReviews);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage())); // 프로필 못찾으면 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "내 리뷰 조회 중 오류 발생: " + e.getMessage()));
        }
    }

    // --- 좋아요 관련 엔드포인트 수정 ---

    @Operation(summary = "리뷰 좋아요/취소 토글", description = "리뷰에 좋아요를 누르거나 취소합니다. (하나의 엔드포인트로 처리)")
    @PostMapping("/{reviewId}/like-toggle") // 엔드포인트명 변경 제안
    public ResponseEntity<?> toggleLikeReview(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            Profile profile = bookReviewService.getProfileByUserId(userId);
            boolean isLiked = bookReviewService.toggleLikeBookReview(reviewId, profile.getProfileId());
            Integer likesCount = bookReviewService.getLikesCount(reviewId);

            String message = isLiked ? "리뷰 좋아요 완료" : "리뷰 좋아요 취소 완료";
            HttpStatus status = isLiked ? HttpStatus.CREATED : HttpStatus.OK; // 좋아요 추가 시 201, 취소 시 200

            return ResponseEntity.status(status).body(Map.of(
                    "message", message,
                    "isLiked", isLiked,
                    "likesCount", likesCount
            ));
        } catch (EntityNotFoundException e) {
            // 리뷰 또는 프로필을 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "좋아요 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    // 기존 좋아요 등록 엔드포인트 (@PostMapping("/{reviewId}/like"))는 제거
    // 기존 좋아요 해제 엔드포인트 (@DeleteMapping("/{reviewId}/like"))는 제거

    @Operation(summary = "리뷰 좋아요 수 조회", description = "리뷰의 좋아요 수를 조회합니다.")
    @GetMapping("/{reviewId}/likes/count") // 일관성을 위해 엔드포인트 변경 제안
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
    @GetMapping("/{reviewId}/likes/status") // 새로운 엔드포인트 추가
    public ResponseEntity<Map<String, Object>> getReviewLikeStatus(
            @PathVariable Integer reviewId
    ) {
        String userId = getCurrentUserId();
        // 비로그인 사용자도 좋아요 상태 확인 가능하도록 (항상 false)
        if (userId == null) {
            return ResponseEntity.ok(Map.of("reviewId", reviewId, "isLiked", false));
        }

        try {
            Profile profile = bookReviewService.getProfileByUserId(userId);
            boolean isLiked = bookReviewService.isReviewLikedByUser(reviewId, profile.getProfileId());
            return ResponseEntity.ok(Map.of("reviewId", reviewId, "isLiked", isLiked));
        } catch (EntityNotFoundException e) {
            // 리뷰를 찾을 수 없거나 프로필을 찾을 수 없는 경우 (하지만 getProfileByUserId에서 에러날 경우 userId가 null 아니므로 다른 예외임)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "isLiked", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "좋아요 상태 조회 중 오류 발생: " + e.getMessage(), "isLiked", false));
        }
    }


    /**
     * 현재 로그인된 사용자의 userId를 가져오는 공통 메서드
     * @return userId (없으면 null)
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }
}