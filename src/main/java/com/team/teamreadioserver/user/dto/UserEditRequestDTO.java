package com.team.teamreadioserver.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// 회원정보 수정용 DTO
@Getter
@Setter
public class UserEditRequestDTO {

  private String userId;
  private String userName;
  private String userPwd;
  public String userEmail;
  public String userPhone;
  public LocalDate userBirthday;

}
