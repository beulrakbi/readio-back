package com.team.teamreadioserver.report.repository;

import com.team.teamreadioserver.report.entity.ReportedReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportedReviewRepository extends JpaRepository<ReportedReview, Integer> { // PK 타입 확인 필수 (Integer 또는 Long)

    Optional<ReportedReview> findByReportId(Integer reportId);

    // ✨ 수정된 부분: 'reviewId' 필드가 ReportedReview에 직접 없으므로,
    // BookReview 엔티티를 참조하는 'bookReview' 필드를 통해 'reviewId'에 접근하도록 변경 ✨
    // Spring Data JPA는 'findBy[관계_엔티티_필드명]_[관계_엔티티의_필드명]' 패턴을 지원합니다.

    ReportedReview findByBookReview_ReviewId(Integer reviewId);
    List<ReportedReview> findByUserId(String userId); // 이 userId는 ReportedReview 엔티티에 직접 있는 필드여야 합니다.
    Page<ReportedReview> findAllBy(Pageable pageable);

  }