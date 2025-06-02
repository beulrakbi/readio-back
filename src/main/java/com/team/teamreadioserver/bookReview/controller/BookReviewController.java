package com.team.teamreadioserver.bookReview.controller;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.service.BookReviewService;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookReview")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @PostMapping("/create")
    public ResponseEntity<String> createBookReview(@RequestBody @Valid ReviewRequestDTO reviewRequestDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        bookReviewService.addBookReview(reviewRequestDTO, userId);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "리뷰 신고 등록", description = "리뷰를 신고합니다.")
    @PutMapping("/{reviewId}/report")
    public ResponseEntity<Void> report(@PathVariable Integer reviewId) {
        bookReviewService.reportReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        bookReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    @Operation(summary = "책 별 리뷰 조회", description = "책 별 리뷰를 조회합니다.")
    @GetMapping("/{bookIsbn}")
    public ResponseEntity<ResponseDTO> getAllBookReviews(@PathVariable String bookIsbn) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Profile profile = bookReviewService.getProfileByUserId(userId);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 프로필을 찾을 수 없습니다.");
        }

        List<MyReviewResponseDTO> myReviews = bookReviewService.myBookReview(profile.getProfileId());
        return ResponseEntity.ok(myReviews);
    }

    @Operation(summary = "좋아요 등록", description = "리뷰에 좋아요를 등록합니다.")
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<String> likeReview(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Profile profile = bookReviewService.getProfileByUserId(userId);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 프로필을 찾을 수 없습니다.");
        }

        bookReviewService.addLikeBookReview(reviewId, profile.getProfileId());
        return ResponseEntity.ok("리뷰 좋아요 완료");
    }

    @Operation(summary = "좋아요 해제", description = "리뷰 좋아요를 해제합니다.")
    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<?> deleteLike(@PathVariable Integer reviewId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Profile profile = bookReviewService.getProfileByUserId(userId);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 프로필을 찾을 수 없습니다.");
        }

        bookReviewService.removeLikeBookReview(reviewId, profile.getProfileId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 수", description = "리뷰의 좋아요 수를 조회합니다.")
    @GetMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<Integer> likesCount(@PathVariable Integer reviewId) {
        Integer count = bookReviewService.getLikesCount(reviewId);
        return ResponseEntity.ok(count);
    }

    /**
     * 현재 로그인된 사용자의 userId를 가져오는 공통 메서드
     * @return userId (없으면 null)
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
