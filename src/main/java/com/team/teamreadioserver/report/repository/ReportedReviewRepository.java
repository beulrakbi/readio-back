package com.team.teamreadioserver.report.repository;

import com.team.teamreadioserver.report.entity.ReportedReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportedReviewRepository extends JpaRepository<ReportedReview, Integer> {
    ReportedReview findByReportId(Integer reportId);
    ReportedReview findByReviewId(Integer reviewId);
    List<ReportedReview> findByUserId(String userId);
    Page<ReportedReview> findAllBy(Pageable pageable);
}
