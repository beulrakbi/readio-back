package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.InterestDiffDTO;
import com.team.teamreadioserver.statistics.dto.InterestTrendDTO;
import com.team.teamreadioserver.statistics.service.InterestTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class InterestTrendController {

    private final InterestTrendService interestTrendService;

    /**
     * 관심 키워드/카테고리 추세 조회
     */
    @GetMapping("/summary/interest-trend")
    public ResponseEntity<List<InterestTrendDTO>> getInterestTrend(
            @RequestParam String type,
            @RequestParam String granularity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "count") String sort,
            @RequestParam(required = false) Integer limit
    ) {
        List<InterestTrendDTO> result = interestTrendService.getInterestTrend(
                type, granularity, startDate, endDate, sort, limit
        );
        return ResponseEntity.ok(result);
    }


    /**
     * 두 달 간 관심 키워드/카테고리 비교
     */
    @GetMapping("/stats/interest-diff")
    public ResponseEntity<List<InterestDiffDTO>> getInterestDiff(
            @RequestParam String type,    // "keyword" or "category"
            @RequestParam String month1,  // e.g. "2025-04"
            @RequestParam String month2,  // e.g. "2025-05"
            @RequestParam(defaultValue = "diff") String sort,
            @RequestParam(required = false) Integer limit
    ) {
        List<InterestDiffDTO> result = interestTrendService.getInterestDiff(
                type, month1, month2, sort, limit
        );
        return ResponseEntity.ok(result);
    }
}
