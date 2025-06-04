package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("book_review_likes"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {("profile_id"), ("review_id")})})
@Getter
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("likes_id"))
    private Integer likesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ("profile_id"), referencedColumnName = ("profile_id"), nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ("review_id"), referencedColumnName = ("review_id"), nullable = false)
    private BookReview bookReview;
}