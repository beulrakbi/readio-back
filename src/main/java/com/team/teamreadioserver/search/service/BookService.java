package com.team.teamreadioserver.search.service;

import com.team.teamreadioserver.search.client.ExternalBookApiClient;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.dto.BooksDTO;
import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

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
        BooksDTO test = null;
        if (combined.size() < 30) {
            // DB 에 책 정보가 있으면 페이징해서 리턴
            test = toPagedDto(new ArrayList<>(combined), page, combined.size());
        }
        else
        {
            return test = toPagedDto(new ArrayList<>(combined), page, combined.size());
        }

        // DB에 없으면 외부 API 호출
        List<BookDTO> apiDTOs = externalClient.fetchBooks(keyword, page, size);
        System.out.println("잘 가져오나?: " + apiDTOs);
        // API 결과를 DB 저장 (중복 ISBN 은 skip)
        // 3) API 결과 중 DB에 이미 있는 ISBN 건은 건너뛰고, 신규 ISBN만 저장
        List<String> isbns = apiDTOs.stream()
                .map(BookDTO::getBookIsbn)
                .collect(Collectors.toList());
        Set<String> existing = new HashSet<>(bookRepository.findAllByBookIsbnIn(isbns)
                .stream()
                .map(Book::getBookIsbn)
                .toList());

        List<Book> toSave = apiDTOs.stream()
                .filter(dto -> !existing.contains(dto.getBookIsbn()))
                .map(this::toEntity)
                .collect(Collectors.toList());

        List<Book> saved = bookRepository.saveAll(toSave);

        // 4) 저장된 것 + 기존 DB에 있던 건을 합쳐서 DTO로 변환
        List<Book> allResults = new ArrayList<>(saved);
        // 기존에 DB에 있던 건도 결과에 포함하려면 아래 주석 해제
        allResults.addAll(bookRepository.findAllByBookIsbnIn(isbns));

        List<BookDTO> resultDTOs = allResults.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        test.getBooks().addAll(resultDTOs);
        System.out.println("testtsetse: " + test.getBooks());
        return test;
//        return new BooksDTO(resultDTOs, resultDTOs.size());
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

    public BookDTO selectBook(String bookIsbn) {

        return modelMapper.map(bookRepository.findById(bookIsbn), BookDTO.class);

    }
}
