package com.team.teamreadioserver.filtering.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDetailDTO;
import com.team.teamreadioserver.filtering.entity.Filtering;
import com.team.teamreadioserver.filtering.service.FilteringService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FilteringController {

    private static final Logger log = LoggerFactory.getLogger(FilteringController.class);
    private final FilteringService filteringService;


    @Operation(summary = "필터링 등록 요청", description = "필터링이 등록됩니다.", tags = { "FilteringController" })
    @PostMapping("/filtering/{groupId}")
    public ResponseEntity<ResponseDTO> insertFilterings(@RequestBody List<FilteringDTO> filteringDTOs, @PathVariable int groupId)
    {
        log.info("[FilteringController] insertFilterings");
        System.out.println("filteringList" + filteringDTOs);
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 등록 성공", filteringService.insertFilterings(filteringDTOs, groupId)));
    }

    @Operation(summary = "필터링 그룹 등록 요청", description = "필터링 그룹이 등록됩니다.", tags = { "FilteringController" })
    @PostMapping("/filtering/create")
    public ResponseEntity<ResponseDTO> insertFilteringGroup(@RequestBody FilteringGroupDTO filteringGroupDTO)
    {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 등록 성공", filteringService.insertFilteringGroup(filteringGroupDTO)));
    }

    @Operation(summary = "필터링 그룹 활성 상태 수정 요청", description = "필터링 그룹 활성 상태가 수정됩니다.", tags = { "FilteringController" })
    @PutMapping("/filtering")
    public ResponseEntity<ResponseDTO> updateFilteringGroupActiveState(@RequestBody FilteringGroupDTO filteringGroupDTO)
    {

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 활성 상태 수정 성공",  filteringService.updateFilteringGroupActiveState(filteringGroupDTO)));

    }

    @Operation(summary = "필터링 수정 요청", description = "필터링이 수정됩니다.", tags = { "FilteringController" })
    @PutMapping("/filtering/edit")
    public ResponseEntity<ResponseDTO> updateFilteringGroupActiveState(@RequestBody FilteringGroupDetailDTO filteringGroupDetailDTO)
    {

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "필터링 수정 성공",  filteringService.updateFilteringGroup(filteringGroupDetailDTO)));

    }


    @Operation(summary = "필터링 그룹 전체 조회", description = "필터링 그룹이 전체 조회됩니다.", tags = { "FilteringController" })
    @GetMapping("/filtering")
    public ResponseEntity<ResponseDTO> selectFilteringGroups(@RequestParam(name="offset", defaultValue = "1") String offset)
    {
        log.info("[FilteringController] selectFilteringGroups : " + offset);
        int total = filteringService.selectFilteringGroups();

        Criteria cri = new Criteria(Integer.valueOf(offset), 10);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();
        pagingResponseDTO.setData(filteringService.selectFilteringGroupWithPaging(cri));
        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 전체 조회 성공", pagingResponseDTO));
    }

    @Operation (summary = "필터링 그룹 상세조회", description = "필터링 그룹이 상세 조회됩니다.", tags = { "FilteringController" })
    @GetMapping("/filtering/{groupId}")
    public ResponseEntity<ResponseDTO> selectFilteringGroup(@PathVariable int groupId)
    {

        FilteringGroupDTO group = filteringService.selectFilteringGroup(groupId);
        List<FilteringDTO> filterings = filteringService.selectFilterings(groupId);

        FilteringGroupDetailDTO result = new FilteringGroupDetailDTO();
        result.setFilteringGroup(group);
        result.setFilterings(filterings);

        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "필터링 상세 조회 성공", result));

    }

    @Operation (summary = "필터링 그룹 삭제", description = "필터링 그룹을 삭제합니다.", tags = { "FilteringController" })
    @DeleteMapping("/filtering/{groupId}")
    public ResponseEntity<ResponseDTO> deleteFilteringGroup(@PathVariable int groupId)
    {
        filteringService.removeFilteringGroup(groupId);
        return ResponseEntity.noContent().build();
    }

}
