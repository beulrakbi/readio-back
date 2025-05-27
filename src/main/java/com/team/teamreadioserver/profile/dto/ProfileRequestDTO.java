package com.team.teamreadioserver.profile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileRequestDTO {

    @NotNull(message = "userId는 필수입니다.")
    private String userId;

    @Size(max = 50, message = "필명은 최대 50자까지 가능합니다.")
    private String penName;

    @Size(max = 300, message = "서재소개는 최대 300자까지 가능합니다.")
    private String biography;

    private String isPrivate;

    private MultipartFile image;
}
