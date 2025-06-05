package com.team.teamreadioserver.post.dto;

import com.team.teamreadioserver.post.entity.Post;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    private int postId;
    private String postTitle;
    private String postContent;
    private String bookIsbn;
    private String isHidden;


    public PostRequestDTO(Post post) {
        this.postId = post.getPostId();
        this.postTitle = post.getPostTitle();
        this.postContent = post.getPostContent();
        this.bookIsbn = post.getBookIsbn();
        this.isHidden = post.getPostHidden();
    }


}
