package com.team.teamreadioserver.post.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDTO {
    private String date; // yyyy-MM-dd
    private int postCount;
    private List<Integer> postIds;
}