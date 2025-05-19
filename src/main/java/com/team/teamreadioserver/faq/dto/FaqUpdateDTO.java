package com.team.teamreadioserver.faq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FaqUpdateDTO {
    private Integer faqId;
    @NotBlank(message = "제목은 공백이 아니어야 합니다.")
    private String faqTitle;
    @NotBlank(message = "내용은 공백이 아니어야 합니다.")
    private String faqContent;
}
