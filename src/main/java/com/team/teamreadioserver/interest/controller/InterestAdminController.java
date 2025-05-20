package com.team.teamreadioserver.interest.controller;

import com.team.teamreadioserver.interest.dto.InterestAdminRequestDTO;
import com.team.teamreadioserver.interest.dto.InterestSaveResultDTO;
import com.team.teamreadioserver.interest.service.InterestAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/interests")
@RequiredArgsConstructor
public class InterestAdminController {

    private final InterestAdminService interestAdminService;

    @PostMapping
    public ResponseEntity<InterestSaveResultDTO> registerInterest(
            @Valid @RequestBody InterestAdminRequestDTO requestDTO) {
        InterestSaveResultDTO result = interestAdminService.registerAll(
                requestDTO.getCategories(),
                requestDTO.getKeywords()
        );
        return ResponseEntity.ok(result);
    }
}
