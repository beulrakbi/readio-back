package com.team.teamreadioserver.video.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.CurationTypeDTO;
import com.team.teamreadioserver.video.service.CurationKeywordsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class CurationKeywordsController {

    private static final Logger log = LoggerFactory.getLogger(CurationKeywordsController.class);
    private final CurationKeywordsService curationKeywordsService;

    @Operation(summary = "큐레이션 타입 조회", description = "큐레이션 타입이 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping("curation/{login}")
    public ResponseEntity<ResponseDTO> selectAllCurationTypes(@PathVariable String login)
    {
        log.info("[CurationKeywordsController] selectCurationTypes");
        List<CurationTypeDTO> result = new ArrayList<>();
        if (login.equals("false"))
        {
            result = curationKeywordsService.selectBasicCurationTypes();

        }
        else
        {
            result = curationKeywordsService.selectAllCurationTypes();
        }
        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 타입 조회 성공", result));
    }



    @Operation(summary = "큐레이션 타입 및 키워드 조회", description = "큐레이션 타입과 키워드가 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping("admin/curation/all")
    public ResponseEntity<ResponseDTO> selectAllCurationTypesAndKeywords()
    {
        log.info("[CurationKeywordsController] selectAllCurationTypesAndKeywords");

        List<CurationDTO> result = curationKeywordsService.selectAllCurationTypesAndKeywords();
        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 전체 조회 성공", result));
    }



    @Operation(summary = "큐레이션 키워드 조회", description = "큐레이션 키워드가 조회됩니다.", tags = { "CurationKeywordsController" })
    @GetMapping("curation/keywords/{userId}/{typeId}")
    public ResponseEntity<ResponseDTO> selectCurationKeywordsByType(@PathVariable int typeId, @PathVariable String userId)
    {
        log.info("[CurationKeywordsController] selectCurationKeywordsByType");

        CurationTypeDTO curationTypeDTO = curationKeywordsService.selectCurationType(typeId);
        List<CurationKeywordsDTO> curationKeywordsDTOS = curationKeywordsService.selectCurationKeywordsByTypeId(typeId, userId);

        CurationDTO result = new CurationDTO(curationTypeDTO, curationKeywordsDTOS);

        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "큐레이션 키워드 조회 성공", result));

    }

    @Operation(summary = "큐레이션 수정", description = "큐레이션이 수정됩니다.", tags = { "CurationKeywordsController" })
    @PutMapping("admin/curation/save")
    public ResponseEntity<ResponseDTO> updateAllCuration(@RequestBody CurationDTO curationDTO)
    {
        log.info("[CurationKeywordsController] updateCurationType");

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "큐레이션 수정 성공",  curationKeywordsService.updateAll(curationDTO)));
    }

//    @Operation(summary = "큐레이션 키워드 추가", description = "큐레이션 키워드가 추가됩니다.", tags = { "CurationKeywordsController" })
//    @PostMapping("/admin/curation/save")
//    public ResponseEntity<ResponseDTO> insertCurationKeywords(@RequestBody CurationDTO curationDTO)
//    {
//        log.info("[CurationKeywordsController] insertCurationKeywords");
//        return ResponseEntity
//                .ok()
//                .body(new ResponseDTO(HttpStatus.OK, "필터링 키워드 수정 성공",  curationKeywordsService.insertCurationKeywords(curationDTO.getCurationKeywords())));
//    }
}
