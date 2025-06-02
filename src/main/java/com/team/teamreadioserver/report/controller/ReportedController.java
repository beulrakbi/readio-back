package com.team.teamreadioserver.report.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.report.service.ReportedService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reported")
@RequiredArgsConstructor
public class ReportedController {

    private static final Logger log = LoggerFactory.getLogger(ReportedController.class);
    private final ReportedService reportedService;

    @Operation(summary = "신고된 리뷰 전체 조회", description = "신고된 리뷰 전체 조회됩니다.", tags = { "ReportedController" })
    @GetMapping("/reviews")
    public ResponseEntity<ResponseDTO> selectReportedReviews(@RequestParam(name="offset", defaultValue = "1") String offset)
    {
        log.info("[ReportedController] selectReportedReviews : " + offset);
        int total = reportedService.allReportedReviewNum();

        Criteria cri = new Criteria(Integer.valueOf(offset), 10);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();
        pagingResponseDTO.setData(reportedService.allReportedReviewWithPaging(cri));
        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "신고된 리뷰 전체 조회 성공", pagingResponseDTO));
    }
}
