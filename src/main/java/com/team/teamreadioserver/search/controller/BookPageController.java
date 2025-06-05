package com.team.teamreadioserver.search.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.service.BookService; // BookService 임포트 확인
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
    public ResponseEntity<ResponseDTO> selectBook(@PathVariable String bookIsbn) {
        log.info("[BookPageController] selectBook 호출, ISBN: {}", bookIsbn); // 로그에 ISBN 추가

        // ❗ [수정] 호출하는 메소드 이름을 변경된 이름으로 수정합니다.
        BookDTO selectedBook = bookService.getBookDetailsByIsbn(bookIsbn);

        if (selectedBook == null) {
            log.warn("[BookPageController] ISBN '{}'에 해당하는 책을 찾을 수 없음.", bookIsbn);
            // 책을 찾지 못한 경우 적절한 응답을 반환합니다. (예: 404 Not Found)
            // ResponseDTO의 생성자나 상태 코드 등을 활용하여 실패 응답을 구성할 수 있습니다.
            // 여기서는 예시로 null을 반환하는 경우를 보여주지만, 실제로는 명확한 실패 응답이 좋습니다.
            // return ResponseEntity.status(HttpStatus.NOT_FOUND)
            // .body(new ResponseDTO(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다.", null));
            // 또는 서비스에서 Optional<BookDTO>를 반환하도록 하고, 여기서 orElseThrow() 등을 사용할 수도 있습니다.
        }

        return ResponseEntity.ok().body(
                new ResponseDTO(HttpStatus.OK, "책 조회 성공", selectedBook)
        );
    }
}