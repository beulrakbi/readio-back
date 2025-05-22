package com.team.teamreadioserver.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequestDTO {

  @NotBlank
  private String userId;

  @NotBlank
  private String userName;

  @NotBlank
  private String userPwd;

  @NotBlank
  @Email
  private String userEmail;

  @NotBlank
  private String userPhone;

  @NotBlank
  private String userBirthday;

}
