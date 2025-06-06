package com.team.teamreadioserver.search.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.dto.BooksDTO;
import com.team.teamreadioserver.search.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174"})
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 도서 검색
    @Operation(summary = "도서 검색", description = "키워드로 도서를 조회합니다.")
    @GetMapping("/search/book")
    public ResponseEntity<ResponseDTO> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (query.isBlank()) {
            BooksDTO empty = new BooksDTO(List.of(), 0);
            return ResponseEntity
                    .ok(new ResponseDTO(HttpStatus.OK, "검색어가 비어있습니다", empty));
        }

        BooksDTO books = bookService.searchBooks(query, page, size);
        return ResponseEntity
                .ok(new ResponseDTO(HttpStatus.OK, "도서 검색 성공", books));
    }

    // 도서 저장
    @Operation(summary = "도서 정보 저장", description = "클라이언트로부터 전송된 도서 정보를 DB에 저장합니다.")
    @PostMapping("/api/search/book")
    public ResponseEntity<ResponseDTO> insertBook(@RequestBody BookDTO bookDto) {
        try {
            BookDTO saved = bookService.saveBook(bookDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDTO(HttpStatus.CREATED, "도서 저장 성공", saved));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "도서 저장 실패", null));
        }
    }

}
