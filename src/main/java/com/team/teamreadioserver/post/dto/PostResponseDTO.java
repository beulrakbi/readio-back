package com.team.teamreadioserver.post.dto;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.entity.Profile;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {

    private Integer postId;
    private String postTitle;
    private String postContent;
    private String bookIsbn;
    private Date postCreatedDate;
    private Profile profileId;
    private int postReported;
    private String postHidden;
    private PostImgDTO postImg;


    public PostResponseDTO(Post post) {
        this.postId = post.getPostId();
        this.postTitle = post.getPostTitle();
        this.postContent = post.getPostContent();
        this.bookIsbn = post.getBookIsbn();
        this.postCreatedDate = post.getPostCreateDate();
        this.profileId = post.getProfile();
        this.postReported = post.getPostReported();
        this.postHidden = post.getPostHidden();
    }


}
