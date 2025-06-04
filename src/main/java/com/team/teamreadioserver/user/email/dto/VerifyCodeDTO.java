package com.team.teamreadioserver.user.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 인증번호 확인용 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeDTO {

  private String email;
  private String code;

}
