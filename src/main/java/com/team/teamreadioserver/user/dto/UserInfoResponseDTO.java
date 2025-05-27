package com.team.teamreadioserver.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// 회원정보 조회용 DTO
@Getter
@Setter
public class UserInfoResponseDTO {

  private String userId;
  private String userName;
//  private String userPwd;
  public String userEmail;
  public String userPhone;
  public LocalDate userBirthday;

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
