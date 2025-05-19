package com.team.teamreadioserver.faq.service;

import com.team.teamreadioserver.faq.dto.FaqCreateDTO;
import com.team.teamreadioserver.faq.dto.FaqUpdateDTO;
import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.faq.repository.FaqRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    public void writeFaq(FaqCreateDTO faqCreateDTO) {
        Faq faq = Faq.builder()
                .faqTitle(faqCreateDTO.getFaqTitle())
                .faqContent(faqCreateDTO.getFaqContent())
                // userId, faqCreateAt는 설정하지 않음 => @PrePersist가 처리
                .build();

        faqRepository.save(faq);
    }

    @Transactional
    public void updateFaq(FaqUpdateDTO faqUpdateDTO) {
        Faq faq = faqRepository.findById(faqUpdateDTO.getFaqId())
                .orElseThrow(() -> new IllegalArgumentException("해당 FAQ가 없습니다."));
        faq.updateFaq(
                faqUpdateDTO.getFaqTitle(),
                faqUpdateDTO.getFaqContent()
        );
    }


}
