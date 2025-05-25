package com.team.teamreadioserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponseDTO {

  private String accessToken;
  private String tokenType = "Bearer";

  public JwtResponseDTO(String accessToken) {
    this.accessToken = accessToken;
  }
}
