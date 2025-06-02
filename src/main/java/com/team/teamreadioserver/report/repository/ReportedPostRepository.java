package com.team.teamreadioserver.report.repository;

import com.team.teamreadioserver.report.entity.ReportedPost;
import com.team.teamreadioserver.report.entity.ReportedReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Integer> {
    ReportedPost findByReportId(Integer reportId);
    List<ReportedPost> findByUserId(String userId);
    Page<ReportedPost> findAllBy(Pageable pageable);
}
