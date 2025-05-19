package com.team.teamreadioserver.filtering.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
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
        System.out.println("테스트");
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "필터링 그룹 등록 성공", filteringService.insertFilteringGroup(filteringGroupDTO)));
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

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", pagingResponseDTO));

    }

    @Operation (summary = "필터링 상세조회", description = "필터링 그룹이 상세 조회됩니다.", tags = { "FilteringController" })
    @GetMapping("/filtering/{groupId}")
    public ResponseEntity<ResponseDTO> selectFilteringGroup(@PathVariable int groupId)
    {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상품 상세정보 조회 성공",  filteringService.selectFilteringGroup(groupId)));
    }


}
