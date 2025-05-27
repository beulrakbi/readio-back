package com.team.teamreadioserver.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private String penName;
    private String biography;
    private String isPrivate;
    private String imageUrl;

}
