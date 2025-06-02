package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.ClickLogDTO;
import com.team.teamreadioserver.statistics.service.ClickLogService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clicks")
public class ClickLogController {

    private final ClickLogService clickLogService;

    @PostMapping
    public ResponseEntity<Void> saveClick(@RequestBody ClickLogDTO dto) {
        clickLogService.saveClick(dto);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/popular")
//    public ResponseEntity<List<?>> getPopularContent(@RequestParam String type) {
//        List<?> popular = clickLogService.getTopClickedContent(type);
//        return ResponseEntity.ok(popular);
//    }



}
