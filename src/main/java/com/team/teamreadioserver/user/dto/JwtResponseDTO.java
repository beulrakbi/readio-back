package com.team.teamreadioserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {

  private String accessToken;
  private String tokenType = "Bearer";
  private String userId;    // 추가
  private String userName;  // 추가

  public JwtResponseDTO(String accessToken) {
    this.accessToken = accessToken;
  }
}
