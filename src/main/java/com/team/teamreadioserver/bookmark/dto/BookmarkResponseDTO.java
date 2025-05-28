package com.team.teamreadioserver.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkResponseDTO {
    private String bookIsbn;
    private String userId;
}
