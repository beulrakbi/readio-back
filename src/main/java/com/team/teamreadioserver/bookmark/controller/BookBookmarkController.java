package com.team.teamreadioserver.bookmark.controller;

import com.team.teamreadioserver.bookmark.dto.BookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import com.team.teamreadioserver.bookmark.service.BookBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookBookmark")
public class BookBookmarkController {
    @Autowired
    private BookBookmarkRepository bookBookmarkRepository;
    @Autowired
    private BookBookmarkService bookBookmarkService;

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기를 등록합니다.")
    @PostMapping("/create")
    public String createBookBookmark(@RequestBody @Valid BookmarkResponseDTO bookmarkResponseDTO){
        bookBookmarkService.addBookBookmark(bookmarkResponseDTO);
        return "즐겨찾기가 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기를 삭제합니다.")
    @DeleteMapping("/delete/{bookmarkId}")
    public String deleteBookBookmark(@PathVariable Integer bookmarkId){
        bookBookmarkService.deleteBookBookmark(bookmarkId);
        return "즐겨찾기 삭제되었습니다.";
    }

    @Operation(summary = "즐겨찾기 조회", description = "즐겨찾기를 조회합니다.")
    @GetMapping("/list")
    public List<BookmarkResponseDTO> bookmarkList(@RequestParam String userId){
        return bookBookmarkService.getUserBookBookmarks(userId);
    }
}
