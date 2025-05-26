package com.team.teamreadioserver.search.service;

import com.team.teamreadioserver.search.dto.BookRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class BookService {

    private static final String ALADIN_API_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String TTB_KEY = "ttbehfvls09271435001";

    public String searchBooks(BookRequestDTO bookRequestDTO) {

        URI uri = UriComponentsBuilder.fromHttpUrl(ALADIN_API_URL)
                .queryParam("ttbkey", TTB_KEY)
                .queryParam("Query", bookRequestDTO.getQuery())
                .queryParam("QueryType", "Title")
                .queryParam("MaxResults", 10)
                .queryParam("SearchTarget", "Book")
                .queryParam("output", "js")
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class); // JSON 그대로 반환
    }
}
