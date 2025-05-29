package com.team.teamreadioserver.search.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.search.dto.BooksDTO;
import com.team.teamreadioserver.search.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174"})
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "도서 검색", description = "키워드로 도서를 조회합니다.")
    @GetMapping("/book")
    public ResponseEntity<ResponseDTO> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        if (query.isBlank()) {
            // 검색어 없으면 빈 결과 내보내기
            BooksDTO empty = new BooksDTO(List.of(), 0);
            return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "검색어가 비어있습니다", empty));
        }

        BooksDTO books = bookService.searchBooks(query, page, size);
        return ResponseEntity.ok(
                new ResponseDTO(HttpStatus.OK, "도서 검색 성공", books)
        );
    }
}
