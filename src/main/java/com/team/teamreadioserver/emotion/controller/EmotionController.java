package com.team.teamreadioserver.emotion.controller;

import com.team.teamreadioserver.emotion.dto.EmotionRequestDTO;
import com.team.teamreadioserver.emotion.dto.EmotionResponseDTO;
import com.team.teamreadioserver.emotion.service.EmotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<?> registerEmotion(@Valid @RequestBody EmotionRequestDTO requestDTOdto) {
        System.out.println("userId: " + requestDTOdto.getUserId());
        System.out.println("emotionType: " + requestDTOdto.getEmotionType());
        System.out.println("date: " + requestDTOdto.getDate());

        emotionService.saveEmotion(requestDTOdto);
        return ResponseEntity.ok("감정 등록 완료");
    }

    @GetMapping
    public ResponseEntity<List<EmotionResponseDTO>> getMonthlyEmotions(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month) {

        List<EmotionResponseDTO> response = emotionService.getEmotionsByMonth(userId, year, month);
        return ResponseEntity.ok(response);
    }


    @PutMapping
    public ResponseEntity<?> updateEmotion(@Valid @RequestBody EmotionRequestDTO requestDTO) {
        emotionService.updateEmotion(requestDTO);
        return ResponseEntity.ok("감정 수정 또는 등록 완료");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEmotion(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "date") String dateStr) {

        LocalDate date = LocalDate.parse(dateStr);
        emotionService.deleteEmotion(userId, date);
        return ResponseEntity.ok("감정 삭제 완료");
    }
}
