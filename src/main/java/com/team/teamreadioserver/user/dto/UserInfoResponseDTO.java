package com.team.teamreadioserver.user.dto;

import lombok.Getter;
import lombok.Setter;

// 회원정보 조회용 DTO
@Getter
@Setter
public class UserInfoResponseDTO {

  private String userId;
  private String userName;
  private String userPwd;
  public String userEmail;
  public String userPhone;
  public String userBirthday;

}
