package com.team.teamreadioserver.bookReview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("book_review_likes"))
@Getter
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("likes_id"))
    private Integer likesId;

    @Column(name = ("profile_id"))
    private Integer profileId;

    @Column(name = ("review_id"))
    private Integer reviewId;
}
