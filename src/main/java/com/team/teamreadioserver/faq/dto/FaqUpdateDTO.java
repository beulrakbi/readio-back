package com.team.teamreadioserver.faq.dto;

import com.team.teamreadioserver.faq.entity.Faq;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FaqUpdateDTO {
    private Integer faqId;
    @NotBlank(message = "제목은 공백이 아니어야 합니다.")
    private String faqTitle;
    @NotBlank(message = "내용은 공백이 아니어야 합니다.")
    private String faqContent;


    public static FaqUpdateDTO detail2(Faq faq) {
        FaqUpdateDTO dto = new FaqUpdateDTO();
        dto.faqId = faq.getFaqId();
        dto.faqTitle = faq.getFaqTitle();
        dto.faqContent = faq.getFaqTitle();
        return dto;
    }
}