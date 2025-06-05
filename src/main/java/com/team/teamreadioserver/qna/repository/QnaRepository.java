package com.team.teamreadioserver.qna.repository;

import com.team.teamreadioserver.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna,Integer> {
    //페이징 처리 (이 메서드는 사용되지 않고 findAll(pageable)이 사용되므로 주석 처리하거나 제거 가능)
    // Page<Qna> findByQnaTitle(String faqTitle, Pageable pageable);

    List<Qna> findAllByOrderByQnaCreateAtDesc();

    List<Qna> findByQnaTitleContainingIgnoreCase(String qnaTitle); // ✨ 파라미터 이름을 qnaTitle로 수정

    List<Qna> findByUserId(String userId);

}