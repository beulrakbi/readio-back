package com.team.teamreadioserver.search.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BooksDTO {
    private List<BookDTO> books;
    private int total; // 전체 매칠된 개수

    public BooksDTO(List<BookDTO> books, int total)
    {
        for (BookDTO book : books) {
            String cleanText = StringEscapeUtils.unescapeHtml4(book.getBookTitle());
            book.setBookTitle(cleanText);
        }

        this.books = books;
        this.total = total;

    }
}