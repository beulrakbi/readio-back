package com.team.teamreadioserver.clicklog.controller;

import com.team.teamreadioserver.clicklog.dto.ClickLogDTO;
import com.team.teamreadioserver.clicklog.service.ClickLogService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
