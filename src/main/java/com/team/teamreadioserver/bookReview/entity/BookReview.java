package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.report.entity.ReportedReview; // ReportedReview import
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("book_review"))
@Getter
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("review_id"))
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ("profile_id"), referencedColumnName = ("profile_id"), nullable = false)
    private Profile profile;

    @Column(name=("book_isbn"), nullable=false)
    private String bookIsbn;

    @Column(name = ("review_content"))
    private String reviewContent;

    @Column(name = ("reported_count"), nullable = false)
    private Integer reportedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = ("is_hidden"), nullable = false)
    private IsHidden isHidden;

    @Column(name = ("created_at"))
    private Date createdAt;

    // ReviewLike와의 OneToMany 관계 (이미 추가하셨을 것으로 예상)
    @OneToMany(mappedBy = "bookReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "bookReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportedReview> reportedReviews = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
        if (this.isHidden == null) {
            this.isHidden = IsHidden.N;
        }
    }
    public void report() {
        this.reportedCount++;
    }
    public void hide() {
        this.isHidden = IsHidden.Y;
    }
}