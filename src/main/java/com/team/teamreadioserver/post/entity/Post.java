package com.team.teamreadioserver.post.entity;

import com.team.teamreadioserver.postReview.entity.PostReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;
    @Column(name = "post_title")
    private String postTitle;
    @Column(name = "post_content")
    private String postContent;
    @Column(name = "book_isbn")
    private String bookIsbn;
    @Column(name = "post_create_at")
    private Date postCreateDate;
    @Column(name = "reported_count")
    private int postReported;
    @Column(name = "is_hidden")
    private String postHidden;

    @Column(name = "profile_id")
    private Integer profile;
    //    @ManyToOne
//    @JoinColumn(name = "profile_id")
    @PrePersist
    public void prePersist() {
        this.profile = 1;
        this.postCreateDate = new Date();
        this.postReported = 0;
        this.postHidden = "";
    }

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PostImg postImg;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private PostReview postReview;
}
