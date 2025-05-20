package com.team.teamreadioserver.interest.controller;

import com.team.teamreadioserver.interest.dto.InterestAdminRequestDTO;
import com.team.teamreadioserver.interest.dto.InterestSaveResultDTO;
import com.team.teamreadioserver.interest.dto.InterestUpdateRequestDTO;
import com.team.teamreadioserver.interest.service.InterestAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/interests")
@RequiredArgsConstructor
public class InterestAdminController {

    private final InterestAdminService interestAdminService;

    //등록
    @PostMapping
    public ResponseEntity<InterestSaveResultDTO> registerInterest(
            @Valid @RequestBody InterestAdminRequestDTO adminRequestDTO) {
        InterestSaveResultDTO result = interestAdminService.registerAll(
                adminRequestDTO.getCategories(),
                adminRequestDTO.getKeywords()
        );
        return ResponseEntity.ok(result);
    }

    //조회
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = interestAdminService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<String>> getAllKeywords() {
        List<String> keywords = interestAdminService.getAllKeywords();
        return ResponseEntity.ok(keywords);
    }

    //수정
    @PutMapping("/category/{interestId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("interestId") Long interestId,
            @Valid @RequestBody InterestUpdateRequestDTO updateRequestDTO) {
        interestAdminService.updateCategory(interestId, updateRequestDTO.getNewName());
        return ResponseEntity.ok("카테고리가 수정되었습니다.");
    }
    @PutMapping("/keyword/{interestKeywordId}")
    public ResponseEntity<?> updateKeyword(
            @PathVariable("interestKeywordId") Long interestKeywordId,
            @Valid @RequestBody InterestUpdateRequestDTO updateRequestDTO) {

        interestAdminService.updateKeyword(interestKeywordId, updateRequestDTO.getNewName());
        return ResponseEntity.ok("키워드가 수정되었습니다.");
    }






}
