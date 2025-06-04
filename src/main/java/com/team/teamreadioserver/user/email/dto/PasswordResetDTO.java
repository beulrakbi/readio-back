package com.team.teamreadioserver.user.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 비밀번호 재설정 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {

  private String userId;
  private String newPassword;

}
