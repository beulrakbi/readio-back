// src/main/java/com/team/teamreadioserver/bookmark/controller/BookBookmarkController.java
package com.team.teamreadioserver.bookmark.controller;

import com.team.teamreadioserver.bookmark.dto.BookBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.BookBookmarkRequestDTO;
import com.team.teamreadioserver.bookmark.dto.BookBookmarkStatusResponseDTO; // <-- BookBookmarkStatusResponseDTO 임포트
import com.team.teamreadioserver.bookmark.service.BookBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookBookmark")
public class BookBookmarkController {
    @Autowired
    private BookBookmarkService bookBookmarkService;

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기를 등록하고 생성된 북마크 ID를 반환합니다.")
    @PostMapping("/create")
    public ResponseEntity<Integer> createBookBookmark(
            @RequestBody @Valid BookBookmarkRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = userDetails.getUsername();

        Integer bookmarkId = bookBookmarkService.addBookBookmark(userId, requestDTO);
        return new ResponseEntity<>(bookmarkId, HttpStatus.CREATED);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기를 삭제합니다.")
    @DeleteMapping("/delete/{bookmarkId}")
    public ResponseEntity<String> deleteBookBookmark(
            @PathVariable Integer bookmarkId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = userDetails.getUsername();
        bookBookmarkService.deleteBookBookmark(userId, bookmarkId);
        return new ResponseEntity<>("즐겨찾기 삭제되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "즐겨찾기 조회", description = "인증된 사용자의 즐겨찾기 목록을 조회합니다.")
    @GetMapping("/list")
    public List<BookBookmarkResponseDTO> bookmarkList(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        String userId = userDetails.getUsername();
        return bookBookmarkService.getUserBookBookmarks(userId);
    }

    // --- 추가: 비디오 컨트롤러의 getVideoBookmarkStatus와 동일하게 구현 (로그인 필요) ---
    @Operation(summary = "책 북마크 상태 및 총 개수 조회 (로그인 필요)", description = "특정 책의 북마크 여부와 총 개수를 조회합니다. 로그인된 사용자 정보 기반.")
    @GetMapping("/status/{bookIsbn}")
    public BookBookmarkStatusResponseDTO getBookBookmarkStatus(
            @PathVariable String bookIsbn,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // userId는 이제 항상 존재함.
        return bookBookmarkService.getBookBookmarkStatus(userId, bookIsbn);
    }

    // --- 추가: 비디오 컨트롤러의 getPublicTotalBookmarkCount와 동일하게 구현 (로그인 불필요) ---
    @Operation(summary = "책의 총 북마크 개수 조회 (로그인 불필요)", description = "로그인 여부와 상관없이 특정 책의 총 북마크 개수를 조회합니다.")
    @GetMapping("/publicCount/{bookIsbn}")
    public long getPublicTotalBookmarkCount(@PathVariable String bookIsbn) {
        return bookBookmarkService.getTotalBookmarkCountOnlyForBook(bookIsbn);
    }
}