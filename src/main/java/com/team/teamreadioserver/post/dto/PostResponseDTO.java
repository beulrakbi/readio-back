package com.team.teamreadioserver.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.teamreadioserver.bookReview.dto.BookDetailsDTO;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.search.dto.BookDTO;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date postCreatedDate;
    private ProfileResponseDTO profileId;
    private int postReported;
    private String postHidden;
    private PostImgDTO postImg;


    private BookDTO bookDetails;
    private Long likes;
    private Long reviewCount;


//    public PostResponseDTO(Post post) {
//        this.postId = post.getPostId();
//        this.postTitle = post.getPostTitle();
//        this.postContent = post.getPostContent();
//        this.bookIsbn = post.getBookIsbn();
//        this.postCreatedDate = post.getPostCreateDate();
//        this.profileId = post.getProfile();
//        this.postReported = post.getPostReported();
//        this.postHidden = post.getPostHidden();
//    }


}
