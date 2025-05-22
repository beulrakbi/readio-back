package com.team.teamreadioserver.faq.service;

import com.team.teamreadioserver.faq.dto.FaqCreateDTO;
import com.team.teamreadioserver.faq.dto.FaqResponseDTO;
import com.team.teamreadioserver.faq.dto.FaqUpdateDTO;
import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.faq.repository.FaqRepository;
import com.team.teamreadioserver.notice.dto.NoticeResponseDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    public List<FaqResponseDTO> getFaqList() {
        List<Faq> faq = faqRepository.findAllByOrderByFaqCreateAtDesc(); // 최신순 정렬

        return faq.stream()
                .map(faq1 -> new FaqResponseDTO(
                        faq1.getFaqId(),
                        faq1.getFaqTitle(),
                        faq1.getFaqCreateAt()
                ))
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void deleteFaq(Integer faqId) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시륵"));
        faqRepository.delete(faq);
    }

    public List<FaqResponseDTO> searchFaqByTitle(String faqTitle) {
        List<Faq> faq = faqRepository.findByFaqTitleContainingIgnoreCase(faqTitle);

        return faq.stream()
                .map(FaqResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public FaqUpdateDTO detail(Integer faqId) {
        return faqRepository.findById(faqId)
                .map(FaqUpdateDTO::detail2)
                .orElseThrow(() -> new IllegalArgumentException("FAQ가 존재하지 않습니다."));
    }

}
