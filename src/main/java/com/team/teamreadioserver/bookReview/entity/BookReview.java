package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer profileId;

    @Column(name=("book_isbn"))
    private String bookIsbn;

    @Column(name = ("review_content"))
    private String reviewContent;

    @Column(name = ("reported_count"))
    private Integer reportedCount;

    @Column(name = "is_hidden")
    private IsHidden isHidden;
}
