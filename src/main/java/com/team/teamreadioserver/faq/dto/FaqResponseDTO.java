package com.team.teamreadioserver.faq.dto;

import com.team.teamreadioserver.faq.entity.Faq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FaqResponseDTO {
    private Integer faqId;
    private String faqTitle;
    private LocalDateTime faqCreateAt;

    public static FaqResponseDTO fromEntity(Faq faq) {
        FaqResponseDTO dto = new FaqResponseDTO();
        dto.faqId = faq.getFaqId();
        dto.faqTitle = faq.getFaqTitle();
        dto.faqCreateAt = faq.getFaqCreateAt();
        return dto;
    }
}
