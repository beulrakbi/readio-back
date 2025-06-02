package com.team.teamreadioserver.report.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.report.dto.ReportedPostDTO;
import com.team.teamreadioserver.report.dto.ReportedReviewDTO;
import com.team.teamreadioserver.report.service.ReportedService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reported")
@RequiredArgsConstructor
public class ReportedController {

    private static final Logger log = LoggerFactory.getLogger(ReportedController.class);
    private final ReportedService reportedService;

    @Operation(summary = "신고된 리뷰 전체 조회", description = "신고된 리뷰 전체 조회됩니다.", tags = { "ReportedController" })
    @GetMapping("/review")
    public ResponseEntity<ResponseDTO> selectReportedReviews(@RequestParam(name="offset", defaultValue = "1") String offset)
    {
        log.info("[ReportedController] selectReportedReviews : " + offset);
        int total = reportedService.allReportedReviewNum();

        Criteria cri = new Criteria(Integer.valueOf(offset), 10);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();
        pagingResponseDTO.setData(reportedService.allReportedReviewWithPaging(cri));
        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));
        System.out.println("testetstestse:" + pagingResponseDTO);
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "신고된 리뷰 전체 조회 성공", pagingResponseDTO));
    }

    @Operation(summary = "신고된 리뷰 조회", description = "신고된 리뷰 조회됩니다.", tags = { "ReportedController" })
    @GetMapping("/review/{reportId}")
    public ResponseEntity<ResponseDTO> selectReportedReviews(@PathVariable Integer reportId)
    {
        ReportedReviewDTO reportedReviewDTO = reportedService.getReportedReview(reportId);
        if (reportedReviewDTO == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST, "신고된 리뷰 조회 실패", null));
        else
            return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "신고된 리뷰 조회 성공", reportedReviewDTO));
    }

    @Operation(summary = "신고된 리뷰 숨김/노출 처리", description = "신고된 리뷰 숨김/노출 상태가 변경됩니다", tags = { "ReportedController" })
    @PutMapping("/review/{reportId}")
    public ResponseEntity<ResponseDTO> updateReportedReviewIsHidden(@PathVariable Integer reportId)
    {
        Object result = reportedService.hideReview(reportId);
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED, "신고된 리뷰 숨김/노출 상태 변경됨", result));
    }

    @Operation(summary = "신고된 포스트 전체 조회", description = "신고된 포스트 전체 조회됩니다.", tags = { "ReportedController" })
    @GetMapping("/post")
    public ResponseEntity<ResponseDTO> selectReportedPosts(@RequestParam(name="offset", defaultValue = "1") String offset)
    {
        log.info("[ReportedController] selectReportedPosts : " + offset);
        int total = reportedService.allReportedPostNum();

        Criteria cri = new Criteria(Integer.valueOf(offset), 10);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();
        pagingResponseDTO.setData(reportedService.allReportedPostWithPaging(cri));
        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "신고된 포스트 전체 조회 성공", pagingResponseDTO));
    }

    @Operation(summary = "신고된 포스트 조회", description = "신고된 포스트 조회됩니다.", tags = { "ReportedController" })
    @GetMapping("/post/{reportId}")
    public ResponseEntity<ResponseDTO> selectReportedPost(@PathVariable Integer reportId)
    {
        ReportedPostDTO reportedPostDTO = reportedService.getReportedPost(reportId);
        if (reportedPostDTO == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST, "신고된 포스트 조회 실패", null));
        else
            return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "신고된 포스트 조회 성공", reportedPostDTO));
    }

    @Operation(summary = "신고된 포스트 숨김/노출 처리", description = "신고된 포스트 숨김/노출 상태가 변경됩니다", tags = { "ReportedController" })
    @PutMapping("/post/{reportId}")
    public ResponseEntity<ResponseDTO> updateReportedPostIsHidden(@PathVariable Integer reportId)
    {
        Object result = reportedService.hidePost(reportId);
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED, "신고된 포스트 숨김/노출 상태 변경됨", result));
    }

}
