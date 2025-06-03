package com.team.teamreadioserver.search.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.teamreadioserver.search.entity.Book;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookDTO {

    @EqualsAndHashCode.Include
    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookCover;
    private String bookDescription;
    private String bookPubdate;

    public BookDTO(Book book)
    {
        this.bookIsbn = book.getBookIsbn();
        this.bookTitle = StringEscapeUtils.unescapeHtml4(book.getBookTitle());
        this.bookAuthor = book.getBookAuthor();
        this.bookPublisher = book.getBookPublisher();
        this.bookCover = book.getBookCover();
        this.bookDescription = StringEscapeUtils.unescapeHtml4(book.getBookDescription());
        this.bookPubdate = book.getBookPubdate().toString();
    }

    public static List<BookDTO> fromApiResponse(String text) {
        try {
            // JSONP 헤더/푸터 제거
            int s = text.indexOf('{');
            int e = text.lastIndexOf('}');
            String json = text.substring(s, e + 1);

            // Jackson ObjectMapper 로 JSON 파싱
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);

            // "item" 배열 노드 가져오기
            JsonNode items = root.get("item");
            List<BookDTO> result = new ArrayList<>();

            // 배열 순회하며 DTO 생성
            if (items != null && items.isArray()) {
                for (JsonNode node : items) {
                    BookDTO bookDTO = new BookDTO();
                    bookDTO.setBookIsbn(node.path("isbn").asText());
                    bookDTO.setBookTitle(StringEscapeUtils.unescapeHtml4(node.path("title").asText()));
                    bookDTO.setBookAuthor(node.path("author").asText());
                    bookDTO.setBookPublisher(node.path("publisher").asText());
                    bookDTO.setBookCover(node.path("cover").asText());
                    bookDTO.setBookDescription(node.path("description").asText(""));

                    bookDTO.setBookPubdate(node.path("pubDate").asText(""));


                    result.add(bookDTO);
                }
            }
            return result;

        } catch (Exception ex) {
            throw new RuntimeException("도서 API 응답 파싱 실패", ex);
        }
    }
}
