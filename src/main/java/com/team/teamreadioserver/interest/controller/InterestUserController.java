package com.team.teamreadioserver.interest.controller;

import com.team.teamreadioserver.interest.dto.user.InterestUserRequestDTO;
import com.team.teamreadioserver.interest.dto.user.InterestUserResponseDTO;
import com.team.teamreadioserver.interest.service.InterestUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/interests")
public class InterestUserController {

    private final InterestUserService interestUserService;

    //등록
    @PostMapping
    public ResponseEntity<?> registerInterests(@Valid @RequestBody InterestUserRequestDTO dto) {
        interestUserService.registerInterests(dto);
        return ResponseEntity.ok("관심사 등록이 완료되었습니다.");
    }

    //조회
    @GetMapping("/{userId}")
    public ResponseEntity<InterestUserResponseDTO> getUserInterests(@PathVariable("userId") String userId) {
        InterestUserResponseDTO response = interestUserService.getInterestsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    //수정 및 기존 내용 삭제 표시
    @PutMapping
    public ResponseEntity<?> updateInterests(@Valid @RequestBody InterestUserRequestDTO dto) {
        interestUserService.updateInterests(dto);
        return ResponseEntity.ok("관심사 수정이 완료되었습니다.");
    }
}
