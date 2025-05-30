package com.team.teamreadioserver.qna.repository;

import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna,Integer> {
    //페이징 처리
    Page<Qna> findByQnaTitle(String faqTitle, Pageable pageable);

    List<Qna> findAllByOrderByQnaCreateAtDesc();

    List<Qna> findByQnaTitleContainingIgnoreCase(String faqTitle);

    List<Qna> findByUserId(String userId);

}
