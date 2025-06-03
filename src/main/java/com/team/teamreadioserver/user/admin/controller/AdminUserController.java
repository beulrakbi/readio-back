package com.team.teamreadioserver.user.admin.controller;

import com.team.teamreadioserver.user.admin.dto.AdminUserListResponse;
import com.team.teamreadioserver.user.admin.dto.AdminUserSearchDTO;
import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import com.team.teamreadioserver.user.admin.dto.RoleUpdateRequestDTO;
import com.team.teamreadioserver.user.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin") // 이 경로가 SecurityConfig의 /admin과 일치
@RequiredArgsConstructor
@Tag(name = "관리자_회원 API", description = "회원목록 관련 API")
public class AdminUserController {

  private final AdminUserService adminUserService;


  // 관리자 메인 페이지에 필요한 데이터를 반환하는 API 엔드포인트
  // 이 메서드가 실행될 때, 이미 Spring Security 필터 체인에서 ROLE_ADMIN 권한이 검증된 상태여야 합니다.
  @GetMapping // /admin 경로에 대한 GET 요청
  public ResponseEntity<Map<String, String>> getAdminDashboardData() {
    Map<String, String> responseData = new HashMap<>();
    responseData.put("message", "관리자 메인 페이지 데이터입니다.");
    responseData.put("status", "성공적으로 로드되었습니다.");
    // 여기에 실제 관리자 통계 데이터 등을 추가하세요.
    // responseData.put("totalUsers", String.valueOf(adminService.getTotalUsers()));
    return ResponseEntity.ok(responseData);
  }

  // 회원목록 조회 (정상)
//  @Operation(summary = "회원목록조회" , description = "회원목록 확인이 가능하다.")
//  @GetMapping("users/list")
//  public ResponseEntity<List<AdminUserViewDTO>> getAdminUserList() {
//    List<AdminUserViewDTO> userList = adminUserService.getAdminUserList();
//    return ResponseEntity.ok(userList);



  @Operation(summary = "회원목록조회", description = "회원 목록을 페이징 및 검색 조건으로 조회합니다.")
  @GetMapping("/users/list")
  public ResponseEntity<Map<String, Object>> getUserList(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String searchType,
      @RequestParam(required = false) String searchValue,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false) String userTypes,
      @RequestParam(required = false) String reportStatus
  ) {
    AdminUserSearchDTO searchDTO = new AdminUserSearchDTO();
    searchDTO.setPage(page);
    searchDTO.setSize(size);
    searchDTO.setSearchType(searchType);
    searchDTO.setSearchValue(searchValue);
    searchDTO.setStartDate(startDate);
    searchDTO.setEndDate(endDate);
    searchDTO.setReportStatus(reportStatus);
//    System.out.println("reportStatus="+ reportStatus);    디버깅
    searchDTO.setUserTypesString(userTypes);  // 문자열 저장

    if (searchDTO.getUserTypesString() != null && !searchDTO.getUserTypesString().isEmpty()) {
      searchDTO.setUserTypes(
          Arrays.asList(searchDTO.getUserTypesString().split(","))
      );
    }

    searchDTO.setReportStatus(reportStatus);

    searchDTO.calculateOffset();  // 페이징용 offset 계산 필수

    List<AdminUserViewDTO> userList = adminUserService.getAdminUserList(searchDTO);
    int total = adminUserService.getAdminUserCount(searchDTO);

    // totalPages 계산
    int totalPages = (int) Math.ceil((double) total / size);

    Map<String, Object> result = new HashMap<>();
    result.put("users", userList);
    result.put("currentPage", page);
    result.put("pageSize", size);
    result.put("total", total);
    result.put("totalPages", totalPages); // 추가

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "회원권한변경", description = "관리자는 회원권한을 변경할 수 있다.")
  @PutMapping("/users/{userId}/role")
  public ResponseEntity<?> updateUserRole(@PathVariable String userId, @RequestBody RoleUpdateRequestDTO requestDTO) {
    adminUserService.changeUserRole(userId, requestDTO.getNewRole());
    System.out.println("변경할 권한: " + requestDTO.getNewRole()); // null 확인용 출력

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "회원 삭제", description = "관리자는 회원을 삭제할 수 있다.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
    adminUserService.deleteUser(userId);
    return ResponseEntity.ok().build();

  }


}