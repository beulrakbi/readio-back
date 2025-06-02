package com.team.teamreadioserver.report.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reported_review")
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportedReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "review_id")
    private int reviewId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "reported_date")
    private Date reportedDate;

    public ReportedReview(int reviewId, String userId)
    {
        this.reviewId = reviewId;
        this.userId = userId;
        this.reportedDate = new Date();
    }

}
