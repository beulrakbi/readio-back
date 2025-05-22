package com.team.teamreadioserver.interest.controller;

import com.team.teamreadioserver.interest.dto.admin.InterestAdminRequestDTO;
import com.team.teamreadioserver.interest.dto.admin.InterestDTO;
import com.team.teamreadioserver.interest.dto.admin.InterestSaveResultDTO;
import com.team.teamreadioserver.interest.dto.admin.InterestUpdateRequestDTO;
import com.team.teamreadioserver.interest.service.InterestAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<InterestDTO>> getAllCategories() {
        try {
            List<InterestDTO> result = interestAdminService.getAllCategoriesWithId();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<InterestDTO>> getAllKeywords() {
        try {
            List<InterestDTO> result = interestAdminService.getAllKeywordsWithId();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    //삭제
    @DeleteMapping("/category/{interestCategoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long interestCategoryId) {
        interestAdminService.deleteCategory(interestCategoryId);
        return ResponseEntity.ok("카테고리가 삭제되었습니다.");
    }

    @DeleteMapping("/keyword/{interestKeywordId}")
    public ResponseEntity<?> deleteKeyword(@PathVariable Long interestKeywordId) {
        interestAdminService.deleteKeyword(interestKeywordId);
        return ResponseEntity.ok("키워드가 삭제되었습니다.");
    }




}
