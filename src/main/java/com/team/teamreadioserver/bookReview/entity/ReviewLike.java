package com.team.teamreadioserver.bookReview.entity;

import com.team.teamreadioserver.profile.entity.Profile; // Profile 엔티티 import
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
        uniqueConstraints = {@UniqueConstraint(columnNames = {("profile_id"), ("review_id")})}) // 유니크 제약 조건 추가
@Getter
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ("likes_id"))
    private Integer likesId;

    @ManyToOne(fetch = FetchType.LAZY) // Profile 엔티티와의 ManyToOne 관계
    @JoinColumn(name = ("profile_id"), referencedColumnName = ("profile_id"), nullable = false)
    private Profile profile; // Profile 엔티티 직접 사용

    @ManyToOne(fetch = FetchType.LAZY) // BookReview 엔티티와의 ManyToOne 관계
    @JoinColumn(name = ("review_id"), referencedColumnName = ("review_id"), nullable = false)
    private BookReview bookReview; // BookReview 엔티티 직접 사용
}