package com.team.teamreadioserver.faq.repository;

import com.team.teamreadioserver.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findAllByOrderByFaqCreateAtDesc();

    List<Faq> findByFaqTitleContainingIgnoreCase(String faqTitle);
}
