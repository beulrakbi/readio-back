package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.BookmarkStatsDTO;
import com.team.teamreadioserver.statistics.service.BookmarkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class BookmarkStatsController {

    private final BookmarkStatsService bookmarkStatsService;
    @GetMapping("/bookmarks")
    public ResponseEntity<?> getTopBookmarks(
            @RequestParam String type,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page
    ) {
        Pageable pageable = (page != null && limit != null)
                ? PageRequest.of(page, limit)
                : Pageable.unpaged();

        Page<BookmarkStatsDTO> pageResult = type.equals("video")
                ? bookmarkStatsService.getTopVideoBookmarks(pageable)
                : bookmarkStatsService.getTopBookBookmarks(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("list", pageResult.getContent());
        response.put("totalCount", pageResult.getTotalElements());

        return ResponseEntity.ok(response);
    }

}
