package com.team.teamreadioserver.user.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

//관리자 조회용 DTO
@Getter
@Setter
public class AdminUserViewDTO {
  private String userId;
  private String userName;
  private String userEmail;
  private String userPhone;
  private String userBirthday;
  private String userRole;        // 권한 (USER/ADMIN 등)
  private String userRoleName;    // 한글 변환된 권한명 (일반회원/관리자/정지회원)
  private int reportCount;    // 신고 횟수(리뷰랑 포스트 둘다? )
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime userEnrollDate;   // 가입일자
}
