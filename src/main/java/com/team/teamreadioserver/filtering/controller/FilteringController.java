package com.team.teamreadioserver.filtering.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.service.FilteringService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FilteringController {

    private static final Logger log = LoggerFactory.getLogger(FilteringController.class);
    private final FilteringService filteringService;

    @Operation(summary = "필터링 등록 요청", description = "필터링이 등록됩니다.", tags = { "FilteringController" })
    @PostMapping("/filtering/{groupId}")
    public ResponseEntity<ResponseDTO> insertFiltering(@RequestBody FilteringDTO filteringDTO, @PathVariable int groupId)
    {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 등록 성공", filteringService.insertFiltering(filteringDTO, groupId)));
    }

    @Operation(summary = "필터링 그룹 등록 요청", description = "필터링 그룹이 등록됩니다.", tags = { "FilteringController" })
    @PostMapping("/filtering/create")
    public ResponseEntity<ResponseDTO> insertFilteringGroup(@RequestBody FilteringGroupDTO filteringGroupDTO)
    {
        System.out.println("테스트");
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 등록 성공", filteringService.insertFilteringGroup(filteringGroupDTO)));
    }
}
