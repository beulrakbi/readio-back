package com.team.teamreadioserver.postReview.entity;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "post_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_review_id")
    private int postReviewId;
    @Column(name = "post_review_content")
    private String  postReviewContent;
    @Column(name = "post_review_create_at")
    private Date postReviewDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(name = "post_review_like")
    private int postReviewLike;

//    @PrePersist
//    public void prePersist() {
//        this.profile = 1;
//    }
}
