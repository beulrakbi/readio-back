package com.team.teamreadioserver.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

// 회원정보 조회용 DTO
@Getter
@Setter
public class UserInfoResponseDTO {

  private String userId;
  private String userName;
  //  private String userPwd;
  private String userEmail;
  private String userPhone;
  private LocalDate userBirthday;
  private String userRole;
  private Long profileId;

  @Override
  public String toString() {
    return "UserInfoResponseDTO{" +
        "userId='" + userId + '\'' +
        ", userName='" + userName + '\'' +
        ", userEmail='" + userEmail + '\'' +
        ", userPhone='" + userPhone + '\'' +
        ", userBirthday='" + userBirthday + '\'' +
        '}';
  }
}
