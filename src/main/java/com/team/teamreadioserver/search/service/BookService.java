package com.team.teamreadioserver.search.service;

import com.team.teamreadioserver.search.client.ExternalBookApiClient;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.dto.BooksDTO;
import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ExternalBookApiClient externalClient;

    @Transactional
    public BooksDTO searchBooks(String keyword, int page, int size) {
        // DB 에서 검색 (제목 + 저자)
        List<Book> byTitle  = bookRepository.findAllByBookTitleContaining(keyword);
        List<Book> byAuthor = bookRepository.findAllByBookAuthorContaining(keyword);

        LinkedHashSet<Book> combined = new LinkedHashSet<>();
        combined.addAll(byTitle);
        combined.addAll(byAuthor);
        System.out.println("page: " + page);
        System.out.println("size: " + size);

        // DB에 결과가 있으면, size 파라미터로 페이징
        if (!combined.isEmpty()) {
            return toPagedDto(new ArrayList<>(combined), page, size);
        }

        // DB에 없으면 외부 API 호출
        List<BookDTO> apiDTOs = externalClient.fetchBooks(keyword, page, size);
        System.out.println("잘 가져오나?: " + apiDTOs);

        // 신규 ISBN만 걸러내서 저장
        List<String> isbns = apiDTOs.stream()
                .map(BookDTO::getBookIsbn)
                .collect(Collectors.toList());
        Set<String> existing = bookRepository.findAllByBookIsbnIn(isbns).stream()
                .map(Book::getBookIsbn)
                .collect(Collectors.toSet());

        List<Book> toSave = apiDTOs.stream()
                .filter(dto -> !existing.contains(dto.getBookIsbn()))
                .map(this::toEntity)
                .collect(Collectors.toList());
        List<Book> saved = bookRepository.saveAll(toSave);

        // 저장된 것 + 기존 DB 데이터를 합치고
        List<Book> allResults = new ArrayList<>(saved);
        allResults.addAll(bookRepository.findAllByBookIsbnIn(isbns));

        // 최종 페이징(여기에도 size 적용)
        return toPagedDto(allResults, page, size);
    }

    private BooksDTO toPagedDto(List<Book> list, int page, int size) {
        int total = list.size();
        List<BookDTO> dtos = list.stream()
                .skip((long)(page - 1) * size)
                .limit(size)
                .map(this::toDto)
                .collect(Collectors.toList());
        return new BooksDTO(dtos, total);
    }

    private BookDTO toDto(Book e) {
        return new BookDTO(
                e.getBookIsbn(),
                e.getBookTitle(),
                e.getBookAuthor(),
                e.getBookPublisher(),
                e.getBookCover(),
                e.getBookDescription(),
                e.getBookPubdate() != null
                        ? e.getBookPubdate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        : ""
        );
    }

    private Book toEntity(BookDTO bookDTO) {
        LocalDate pubDate = null;
        String text = bookDTO.getBookPubdate();
        if (text != null && !text.isBlank()) {
            pubDate = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return Book.builder()
                .bookIsbn(bookDTO.getBookIsbn())
                .bookTitle(bookDTO.getBookTitle())
                .bookAuthor(bookDTO.getBookAuthor())
                .bookPublisher(bookDTO.getBookPublisher())
                .bookCover(bookDTO.getBookCover())
                .bookDescription(bookDTO.getBookDescription())
                .bookPubdate(pubDate)
                .build();
    }
}
