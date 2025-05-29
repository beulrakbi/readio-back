package com.team.teamreadioserver.search.client;

import com.team.teamreadioserver.search.dto.BookDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ExternalBookApiClient {
    /*
    * 백엔드 전용 클라이언트 ( 알라딘 API 호출 담당 )
    * */

    private final RestTemplate rest = new RestTemplate();

    @Value("${aladin.ttbkey}")
    private String ttbKey;

    /**
     * Aladin TTB API 호출 → JSONP 헤더/푸터 제거 → DTO 리스트 반환
     */
    public List<BookDTO> fetchBooks(String keyword, int page, int size) {
        int start = (page - 1) * size + 1;
        String target = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx"
                + "?ttbkey=" + ttbKey
                + "&Query=" + keyword
                + "&QueryType=Title"
                + "&MaxResults=" + size
                + "&start=" + start
                + "&SearchTarget=Book"
                + "&output=js"
                + "&Version=20131101";

        String body = rest.getForObject(target, String.class);

        List<BookDTO> bookDTOS = BookDTO.fromApiResponse(body);

        return bookDTOS;
    }
}
