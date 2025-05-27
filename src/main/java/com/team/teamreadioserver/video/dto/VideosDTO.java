package com.team.teamreadioserver.video.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class VideosDTO {
    private List<VideoDTO> videoDTOList;
    private int num;
}
