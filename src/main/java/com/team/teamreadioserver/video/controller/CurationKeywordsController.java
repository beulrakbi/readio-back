package com.team.teamreadioserver.video.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDetailDTO;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.CurationTypeDTO;
import com.team.teamreadioserver.video.service.CurationKeywordsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curation")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CurationKeywordsController {

    private static final Logger log = LoggerFactory.getLogger(CurationKeywordsController.class);
    private final CurationKeywordsService curationKeywordsService;

    @Operation(summary = "큐레이션 타입 조회", description = "큐레이션 타입이 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping
    public ResponseEntity<ResponseDTO> selectCurationTypes()
    {
        log.info("[CurationKeywordsController] selectCurationTypes");

        List<CurationTypeDTO> result = curationKeywordsService.selectAllCurationTypes();
        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 키워드 조회 성공", result));
    }

    @Operation(summary = "큐레이션 타입 조회", description = "큐레이션 타입이 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> selectAllCurationTypesAndKeywords()
    {
        log.info("[CurationKeywordsController] selectAllCurationTypesAndKeywords");

        List<CurationDTO> result = curationKeywordsService.selectAllCurationTypesAndKeywords();
        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 전체 조회 성공", result));
    }



    @Operation(summary = "큐레이션 키워드 조회", description = "큐레이션 키워드가 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping("/{typeId}")
    public ResponseEntity<ResponseDTO> selectCurationKeywordsByType(@PathVariable int typeId)
    {
        log.info("[CurationKeywordsController] selectCurationKeywordsByType");

        CurationTypeDTO curationTypeDTO = curationKeywordsService.selectCurationType(typeId);
        List<CurationKeywordsDTO> curationKeywordsDTOS = curationKeywordsService.selectCurationKeywordsByTypeId(typeId);

        CurationDTO result = new CurationDTO(curationTypeDTO, curationKeywordsDTOS);

        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 키워드 조회 성공", result));

    }

    @Operation(summary = "큐레이션 타입 수정", description = "큐레이션 타입이 수정됩니다.", tags = { "CurationKeywordsController" })
    @PutMapping("/save")
    public ResponseEntity<ResponseDTO> updateCurationType(@RequestBody CurationDTO curationDTO)
    {
        log.info("[CurationKeywordsController] updateCurationType");

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 활성 상태 성공",  curationKeywordsService.updateCurationType(curationDTO.getCurationType())));
    }

    @Operation(summary = "큐레이션 키워드 추가", description = "큐레이션 키워드가 추가됩니다.", tags = { "CurationKeywordsController" })
    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> insertCurationKeywords(@RequestBody CurationDTO curationDTO)
    {

    }
}
