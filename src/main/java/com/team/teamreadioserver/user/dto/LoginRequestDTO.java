package com.team.teamreadioserver.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String userId;
    private String userPwd;
}
