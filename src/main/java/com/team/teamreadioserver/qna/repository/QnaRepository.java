package com.team.teamreadioserver.qna.repository;

import com.team.teamreadioserver.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna,Integer> {
    //페이징 처리
    Page<Qna> findByQnaTitle(String faqTitle, Pageable pageable);
}
