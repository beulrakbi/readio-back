package com.team.teamreadioserver;

import com.team.teamreadioserver.faq.dto.FaqCreateDTO;
import com.team.teamreadioserver.faq.dto.FaqUpdateDTO;
import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.faq.repository.FaqRepository;
import com.team.teamreadioserver.faq.service.FaqService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FaqApplicationTests {
    @Autowired
    private FaqService faqService;
    @Autowired
    private FaqRepository faqRepository;

    @Test
    void contextLoads() {
        FaqCreateDTO dto = new FaqCreateDTO(
                null,
                "테스트 제목",
                "테스트 내용"
        );

        faqService.writeFaq(dto);
    }

    @Test
    void testUpdateFaq() {
        List<Faq> faq = faqRepository.findAll();
        assertFalse(faq.isEmpty());
        Faq savedFaq = faq.get(1);

        FaqUpdateDTO dto = new FaqUpdateDTO(
                savedFaq.getFaqId(),
                "FAQ 수정 테스트",
                "FAQ 수정 테스트"
        );
        faqService.updateFaq(dto);

        Faq updatedFaq = faqRepository.findById(savedFaq.getFaqId()).get();
        assertEquals("FAQ 수정 테스트", updatedFaq.getFaqTitle());
        assertEquals("FAQ 수정 테스트", updatedFaq.getFaqContent());
    }

    @Test
    void testDeleteFaq() {
        List<Faq> faqs = faqRepository.findAll();
        assertFalse(faqs.isEmpty());
        Faq savedFaq = faqs.get(faqs.size() - 1);

        faqService.deleteFaq(savedFaq.getFaqId());

        boolean exists = faqRepository.findById(savedFaq.getFaqId()).isPresent();
        assertFalse(exists);
    }
}
