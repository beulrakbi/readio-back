package com.team.teamreadioserver.post.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostImgDTO {

    private int imgId;
    private String  originalName;
    private String  saveName;
    private int postId;

    public PostImgDTO(String saveName, String originalName) {
        this.saveName = saveName;
        this.originalName = originalName;
    }
}
