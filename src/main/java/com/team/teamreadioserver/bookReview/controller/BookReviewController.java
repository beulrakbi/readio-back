package com.team.teamreadioserver.bookReview.controller;

import com.team.teamreadioserver.bookReview.dto.AllReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.MyReviewResponseDTO;
import com.team.teamreadioserver.bookReview.dto.ReviewRequestDTO;
import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.bookReview.service.BookReviewService;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.search.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookReview")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @PostMapping("/create")
    public String createBookReview(@RequestBody @Valid ReviewRequestDTO reviewRequestDTO) {
        bookReviewService.addBookReview(reviewRequestDTO);
        return "리뷰가 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "리뷰 신고 등록", description = "리뷰를 신고합니다.")
    @PutMapping("/{reviewId}/report")
    public ResponseEntity<Void> report(@PathVariable Integer reviewId) {
        bookReviewService.reportReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @DeleteMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Integer reviewId) {
        bookReviewService.deleteReview(reviewId);
        return "리뷰가 삭제되었습니다.";
    }

    @Operation(summary = "책 별 리뷰 조회", description = "책 별 리뷰를 조회합니다.")
    @GetMapping("/{bookIsbn}")
    public ResponseEntity<ResponseDTO> getAllBookReviews(@PathVariable String bookIsbn)
    {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "리뷰 조회 성공", bookReviewService.getBookReviewByBookIsbn(bookIsbn)));
    }

//    @Operation(summary = "전체 리뷰 조회", description = "전체 리뷰를 조회합니다.")
//    @GetMapping("/reviews")
//    public List<BookReview> allReview() {
//        return bookReviewRepository.findAll();
//    }
//    @Operation(summary = "나의 리뷰 조회", description = "내가 쓴 리뷰를 조회합니다.")
//    @GetMapping("/reviews/my")
//    public List<BookReview> myBookreview(Integer profileId) {
//        return bookReviewRepository.findByProfileId(profileId);
//    }
    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping("/reviews")
    public List<AllReviewResponseDTO> getAllReviews() {
        return bookReviewService.allBookReview();
    }

    @Operation(summary = "내 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/reviews/my")
    public List<MyReviewResponseDTO> getMyReviews(@RequestParam Integer profileId) {
        return bookReviewService.myBookReview(profileId);
    }

    @Operation(summary = "좋아요 등록", description = "좋아요가 등록되었습니다.")
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<String> likeReview(@PathVariable Integer reviewId, @RequestParam @Valid Integer profileId) {
        bookReviewService.addLikeBookReview(reviewId, profileId);
        return ResponseEntity.ok("리뷰 좋아요 완료");
    }

    @Operation(summary = "좋아요 해제", description = "좋아요 해제")
    @DeleteMapping("/review/{reviewId}/like")
    public ResponseEntity<?> deleteLike(@PathVariable Integer reviewId, @RequestParam Integer profileId) {
        // userId와 reviewId를 기반으로 좋아요 삭제 로직 수행
        bookReviewService.removeLikeBookReview(reviewId, profileId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 수", description = "좋아요 수 조회")
    @GetMapping("/reviews/{reviewId}/likes")
    public Integer likesCount(@RequestParam Integer reviewId) {
        return bookReviewService.getLikesCount(reviewId);
    }
}
