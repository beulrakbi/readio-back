package com.team.teamreadioserver.search.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BooksDTO {
    private List<BookDTO> books;
    private int total; // 전체 매칠된 개수
}