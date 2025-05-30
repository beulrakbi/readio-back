package com.team.teamreadioserver.search.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookPage")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174"})  // 프론트 도메인만 허용
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;
    private static final Logger log = LoggerFactory.getLogger(BookPageController.class);

    @Operation(summary = "책 조회 요청", description = "책 상세정보가 조회됩니다.", tags = { "BookPageController" })
    @GetMapping("/{bookIsbn}")
    public ResponseEntity<ResponseDTO> selectBook(@PathVariable String bookIsbn)
    {
        log.info("[BookPageController] selectBook");
        BookDTO selectedBook = bookService.selectBook(bookIsbn);


            return ResponseEntity.ok().body(
                    new ResponseDTO(HttpStatus.OK, "책 조회 성공", selectedBook));
    }

}
