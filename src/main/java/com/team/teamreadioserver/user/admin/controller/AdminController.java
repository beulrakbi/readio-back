package com.team.teamreadioserver.user.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


  @RestController
  @RequestMapping("/admin") // 이 경로가 SecurityConfig의 /admin과 일치
  public class AdminController {

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
}
