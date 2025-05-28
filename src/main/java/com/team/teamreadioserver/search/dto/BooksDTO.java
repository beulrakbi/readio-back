package com.team.teamreadioserver.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooksDTO {

    private List<BookDTO> books;
    private int total; // 전체 매칠된 개수
}
