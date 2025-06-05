package com.team.teamreadioserver.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "reported_post")
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportedPost {
    @Id
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "reported_date")
    private Date reportedDate;

    public ReportedPost(int postId, String userId) {
        this.postId = postId;
        this.userId = userId;
        this.reportedDate = new Date();
    }

}