package com.team.teamreadioserver.statistics.controller;

import com.team.teamreadioserver.statistics.dto.UserBehaviorLogDTO;
import com.team.teamreadioserver.statistics.service.UserBehaviorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserBehaviorLogController {

    private final UserBehaviorLogService userBehaviorLogService;

    @PostMapping("/user-behavior_log")
    public ResponseEntity<Void> saveUserBehaviorLog(@RequestBody UserBehaviorLogDTO dto) {
        userBehaviorLogService.saveUserBehaviorLog(dto);
        return ResponseEntity.ok().build();
    }
}
