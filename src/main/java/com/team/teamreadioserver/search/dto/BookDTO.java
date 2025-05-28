package com.team.teamreadioserver.search.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookCover;
    private String bookDescription;
    private String bookPubdate;

    public static List<BookDTO> fromApiResponse(String text) {
        try {
            // 1) JSONP 헤더/푸터 제거
            int s = text.indexOf('{');
            int e = text.lastIndexOf('}');
            String json = text.substring(s, e + 1);

            // 2) Jackson ObjectMapper 로 JSON 파싱
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);

            // 3) "item" 배열 노드 가져오기
            JsonNode items = root.get("item");
            List<BookDTO> result = new ArrayList<>();

            // 4) 배열 순회하며 DTO 생성
            if (items != null && items.isArray()) {
                for (JsonNode node : items) {
                    BookDTO bookDTO = new BookDTO();
                    bookDTO.setBookIsbn(node.path("isbn").asText());
                    bookDTO.setBookTitle(node.path("title").asText());
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
