package com.team.teamreadioserver.bookmark.controller;

import com.team.teamreadioserver.bookmark.dto.VideoBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.repository.VideoBookmarkRepository;
import com.team.teamreadioserver.bookmark.service.VideoBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videoBookmark")
public class VideoBookmarkController {
    @Autowired
    private VideoBookmarkRepository videoBookmarkRepository;
    @Autowired
    private VideoBookmarkService videoBookmarkService;

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기를 등록합니다.")
    @PostMapping("/create")
    public String createVideoBookmark(@RequestBody @Valid VideoBookmarkResponseDTO videoBookmarkResponseDTO) {
        videoBookmarkService.addVideoBookmark(videoBookmarkResponseDTO);
        return "즐겨찾기가 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기를 삭제합니다.")
    @DeleteMapping("/delete/{bookmarkId}")
    public String deleteVideoBookmark(@PathVariable Integer bookmarkId) {
        videoBookmarkService.deleteVideoBookmark(bookmarkId);
        return "즐겨찾기 삭제되었습니다.";
    }

    @Operation(summary = "즐겨찾기 조회", description = "즐겨찾기가 조회됩니다.")
    @GetMapping("/list")
    public List<VideoBookmarkResponseDTO> bookmarkList(@RequestParam String userId){
        return videoBookmarkService.getUserVideoBookmarks(userId);
    }

}
