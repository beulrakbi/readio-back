package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.ClickedContentDTO;
import com.team.teamreadioserver.statistics.service.ClickAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/analytics")
public class ClickAnalyticsController {

    private final ClickAnalyticsService clickAnalyticsService;

    @GetMapping("/clicks")
    public ResponseEntity<List<ClickedContentDTO>> getClickStats(
            @RequestParam String type,
            @RequestParam String sort,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        List<ClickedContentDTO> result = clickAnalyticsService.getClickedContentList(
                type, sort, startDate, endDate, limit, page, size
        );
        return ResponseEntity.ok(result);
    }
}
