package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.ClickedContentDTO;
import com.team.teamreadioserver.statistics.service.ClickAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/analytics")
public class ClickAnalyticsController {

    private final ClickAnalyticsService clickAnalyticsService;
    @GetMapping("/clicks")
    public ResponseEntity<?> getClickStats(
            @RequestParam String type,
            @RequestParam String sort,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);

        // 날짜가 없을 경우 기본값 설정
        if (startDate == null || endDate == null) {
            startDate = LocalDate.now().minusWeeks(1);
            endDate = LocalDate.now();
        }

        Page<ClickedContentDTO> pageResult = clickAnalyticsService.getClickedContentList(
                type, sort, startDate, endDate, pageable
        );

        Map<String, Object> response = new HashMap<>();
        response.put("list", pageResult.getContent());
        response.put("totalCount", pageResult.getTotalElements());

        return ResponseEntity.ok(response);
    }

}
