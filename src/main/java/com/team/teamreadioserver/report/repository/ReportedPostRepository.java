package com.team.teamreadioserver.report.repository;

import com.team.teamreadioserver.report.entity.ReportedPost;
import com.team.teamreadioserver.report.entity.ReportedReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Integer> {
}
