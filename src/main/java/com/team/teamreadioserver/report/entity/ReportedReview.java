package com.team.teamreadioserver.report.entity;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "reported_review")
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ReportedReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "review_id", nullable = false)
    private BookReview bookReview;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "reported_date")
    @CreationTimestamp
    private Date reportedDate;

    public ReportedReview(BookReview bookReview, String userId)
    {
        this.bookReview = bookReview;
        this.userId = userId;
        this.reportedDate = new Date();
    }

}
