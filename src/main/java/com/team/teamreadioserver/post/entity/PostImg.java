package com.team.teamreadioserver.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_img")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private int imgId;
    @Column(name = "original_name")
    private String originalName;
    @Column(name = "saved_name")
    private String savedName;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostImg(Post post, String savedName, String originalName) {
        this.post = post;
        this.savedName = savedName;
        this.originalName = originalName;
    }
}
