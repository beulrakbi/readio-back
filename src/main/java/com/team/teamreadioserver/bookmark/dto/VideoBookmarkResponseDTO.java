package com.team.teamreadioserver.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoBookmarkResponseDTO {
    private String videoId;
    private String userId;
}
