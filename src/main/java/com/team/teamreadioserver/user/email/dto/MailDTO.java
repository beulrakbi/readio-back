package com.team.teamreadioserver.user.email.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {

  @Schema(description = "인증을 요청할 이메일 주소", example = "user@example.com")
  private String userId;
  private String email;

}
