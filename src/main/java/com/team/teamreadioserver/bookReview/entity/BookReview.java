package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.team.teamreadioserver.bookReview.enumPackage.IsHidden.Y;

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

    @Column(name = ("profile_id"))
    private Long profileId;

    @Column(name=("book_isbn"))
    private String bookIsbn;

    @Column(name = ("review_content"))
    private String reviewContent;

    @Column(name = ("reported_count"))
    private Integer reportedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = ("is_hidden"))
    private IsHidden isHidden;

    @Column(name = ("created_at"))
    private Date createdAt;

    @PrePersist
    public void prePersist(){
        this.isHidden = isHidden.N;
        this.profileId = 1L;
//        this.reportedCount = 0;
    }
    public void report() {
        this.reportedCount++;
    }
    public String hide() {

        if (this.isHidden == isHidden.N)
        {
            this.isHidden = isHidden.Y;
            return "숨김처리됨";
        }
        else
        {
            this.isHidden = isHidden.N;
            return "노출처리됨";
        }

    }
}
