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
import org.springframework.security.core.Authentication; // Authentication import
import org.springframework.security.core.context.SecurityContextHolder; // SecurityContextHolder import
import org.springframework.web.bind.annotation.*;
// import com.team.teamreadioserver.user.entity.User; // User 엔티티 import 제거 (혹은 주석 처리)
// import org.springframework.security.core.annotation.AuthenticationPrincipal; // AuthenticationPrincipal import 제거 (혹은 주석 처리)

import java.util.List;

@RestController
@RequestMapping("/bookReview")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @PostMapping("/create")
    public ResponseEntity<String> createBookReview(
            @RequestBody @Valid ReviewRequestDTO reviewRequestDTO
            // @AuthenticationPrincipal User currentUser // <-- 이 부분을 삭제하거나 주석 처리
    ) {
        // ---- SecurityContextHolder에서 직접 Authentication 객체 가져오기 (notice 모듈 방식) ----
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 디버그 로그는 그대로 유지
        System.out.println("컨트롤러 진입 - 인증 객체 존재 여부: " + (authentication != null));
        if (authentication != null) {
            System.out.println("컨트롤러 진입 - 인증 객체 이름: " + authentication.getName());
            System.out.println("컨트롤러 진입 - 인증 객체 인증됨: " + authentication.isAuthenticated());
            System.out.println("컨트롤러 진입 - 인증 객체 권한: " + authentication.getAuthorities());
        }
        // ----------------------------------------------------------------------------------

        // 인증 여부 확인 및 userId 추출
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 인증 안 됨
        }
        String userId = authentication.getName(); // 인증된 사용자의 userId (principal 이름)

        // 익명 사용자(AnonymousAuthenticationToken) 필터링 (선택 사항, 필요 시)
        // if (authentication instanceof AnonymousAuthenticationToken) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        // }
        // String userId = authentication.getName();


        bookReviewService.addBookReview(reviewRequestDTO, userId);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }

    // --- 나머지 컨트롤러 메서드들도 동일하게 profileId를 @AuthenticationPrincipal Long profileId 대신
    // --- SecurityContextHolder.getContext().getAuthentication().getName()을 통해 userId를 얻은 후
    // --- profileRepository.findByUser_UserId(userId)로 profileId를 얻는 방식으로 변경해야 합니다.
    // --- 또는 Notice처럼 @AuthenticationPrincipal을 아예 안 쓰고, 서비스에서 profileId를 찾도록 만들 수 있습니다.

    @Operation(summary = "리뷰 신고 등록", description = "리뷰를 신고합니다.")
    @PutMapping("/{reviewId}/report")
    public ResponseEntity<Void> report(@PathVariable Integer reviewId) {
        bookReviewService.reportReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        bookReviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    @Operation(summary = "책 별 리뷰 조회", description = "책 별 리뷰를 조회합니다.")
    @GetMapping("/{bookIsbn}")
    public ResponseEntity<ResponseDTO> getAllBookReviews(@PathVariable String bookIsbn) {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "리뷰 조회 성공", bookReviewService.getBookReviewByBookIsbn(bookIsbn)));
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<List<AllReviewResponseDTO>> getAllReviews() {
        List<AllReviewResponseDTO> reviews = bookReviewService.allBookReview();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/reviews/my")
    public ResponseEntity<List<MyReviewResponseDTO>> getMyReviews(
            // @AuthenticationPrincipal Long profileId // <-- 이 부분을 삭제하거나 주석 처리
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authentication.getName();
        // userId를 통해 profileId를 얻어야 합니다.
        // 이 부분은 Service Layer에서 ProfileRepository를 통해 찾는 것이 더 적절합니다.
        // 예를 들어, noticeService에 있던 userId를 @PrePersist에서 자동 세팅하는 것처럼
        // BookReviewService.myBookReview(userId)로 파라미터 변경 후 서비스에서 profile 찾기.
        // 아니면 여기서 직접 profileRepository 주입받아 profileId 찾기.
        // 여기서는 예시로 profileRepository.findByUser_UserId(userId).profileId 를 사용한다고 가정.
        // 정확한 profileId를 가져오는 로직은 사용자 시스템에 따라 달라집니다.
        Profile profile = bookReviewService.getProfileByUserId(userId); // BookReviewService에 추가할 메서드
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 프로필 없음
        }

        List<MyReviewResponseDTO> myReviews = bookReviewService.myBookReview(profile.getProfileId());
        return ResponseEntity.ok(myReviews);
    }

    @Operation(summary = "좋아요 등록", description = "좋아요가 등록되었습니다.")
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<String> likeReview(
            @PathVariable Integer reviewId
            // @AuthenticationPrincipal Long profileId // <-- 이 부분을 삭제하거나 주석 처리
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userId = authentication.getName();
        Profile profile = bookReviewService.getProfileByUserId(userId); // BookReviewService에 추가할 메서드
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 프로필을 찾을 수 없습니다.");
        }

        bookReviewService.addLikeBookReview(reviewId, profile.getProfileId());
        return ResponseEntity.ok("리뷰 좋아요 완료");
    }

    @Operation(summary = "좋아요 해제", description = "좋아요 해제")
    @DeleteMapping("/review/{reviewId}/like")
    public ResponseEntity<?> deleteLike(
            @PathVariable Integer reviewId
            // @AuthenticationPrincipal Long profileId // <-- 이 부분을 삭제하거나 주석 처리
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String userId = authentication.getName();
        Profile profile = bookReviewService.getProfileByUserId(userId); // BookReviewService에 추가할 메서드
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 프로필을 찾을 수 없습니다.");
        }

        bookReviewService.removeLikeBookReview(reviewId, profile.getProfileId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 수", description = "좋아요 수 조회")
    @GetMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<Integer> likesCount(@PathVariable Integer reviewId) {
        Integer count = bookReviewService.getLikesCount(reviewId);
        return ResponseEntity.ok(count);
    }
}