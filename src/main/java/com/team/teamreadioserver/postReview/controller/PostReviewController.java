package com.team.teamreadioserver.postReview.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.postReview.dto.PostReviewRequestDTO;
import com.team.teamreadioserver.postReview.service.PostReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostReviewController {

    private final PostReviewService postReviewService;

    @Operation(summary = "포스트 리뷰 등록 요청", description = "해당 포스트 리뷰 등록이 진행됩니다.", tags = {"PostReviewController"})
    @PostMapping("/{postId}/reviews")
    public ResponseEntity<ResponseDTO> insertPostReview(@PathVariable("postId") Integer postId,
                                                        @RequestBody PostReviewRequestDTO postReviewRequestDTO) {

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "리뷰 입력 성공", postReviewService.insertPostReview(postReviewRequestDTO, postId)));
    }

    @Operation(summary = "포스트 리뷰 조회 요청", description = "해당 포스트에 등록된 리뷰 리스트 조회가 진행됩니다.", tags = {"ReviewController"})
    @GetMapping("/{postId}/reviews")
    public ResponseEntity<ResponseDTO> selectPostReview(@PathVariable String  postId,
                                                        @RequestParam(name = "offset", defaultValue = "1") String offset) {

        Criteria cri = new Criteria(Integer.valueOf(offset), 5);
        cri.setSearchValue(postId);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();

        int total = (int)postReviewService.selectPostReviewTotal(Integer.valueOf(cri.getSearchValue()));

        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));
        pagingResponseDTO.setData(postReviewService.selectPostReview(cri));

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "조회 성공", pagingResponseDTO));
    }
    @Operation(summary = "포스트 리뷰 수정 요청", description = "리뷰 작성자의 리뷰 수정이 진행됩니다.", tags = {"ReviewController"})
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseDTO> updatePostReview(@RequestBody PostReviewRequestDTO postReviewRequestDTO) {
        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "리뷰 수정 성공", postReviewService.updatePostReview(postReviewRequestDTO)));
    }

    @Operation(summary = "포스트 리뷰 삭제 요청", description = "리뷰 작성자 또는 관리자가 리뷰를 삭제합니다.", tags = {"ReviewController"}) // swagger 태그 확인
    @DeleteMapping("/reviews/{reviewId}") // 엔드포인트 예시: /post/reviews/1
    public ResponseEntity<ResponseDTO> deletePostReview(@PathVariable int reviewId ) {

        postReviewService.deletePostReview(reviewId);

        return ResponseEntity
                .ok() // 또는 204 No Content (삭제 성공 시 내용 없음)
                .body(new ResponseDTO(HttpStatus.OK, "리뷰 삭제 성공", null));
    }
}
