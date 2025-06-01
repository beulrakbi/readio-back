package com.team.teamreadioserver.bookmark.controller;

import com.team.teamreadioserver.bookmark.dto.VideoBookmarkRequestDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkStatusResponseDTO;
import com.team.teamreadioserver.bookmark.service.VideoBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Optional 임포트는 필요 없어지지만, 일단 유지.

@RestController
@RequestMapping("/videoBookmark")
public class VideoBookmarkController {
    @Autowired
    private VideoBookmarkService videoBookmarkService;

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기를 등록하고 갱신된 총 북마크 수를 반환합니다.")
    @PostMapping("/create")
    public long createVideoBookmark(
            @RequestBody @Valid VideoBookmarkRequestDTO videoBookmarkRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails // Optional 제거, UserDetails로 복귀
    ) {
        String userId = userDetails.getUsername();
        return videoBookmarkService.addVideoBookmark(userId, videoBookmarkRequestDTO);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기를 삭제하고 갱신된 총 북마크 수 및 북마크 ID를 반환합니다.")
    @DeleteMapping("/delete/{bookmarkId}")
    public VideoBookmarkStatusResponseDTO deleteVideoBookmark( // long -> VideoBookmarkStatusResponseDTO
                                                               @PathVariable Integer bookmarkId,
                                                               @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        return videoBookmarkService.deleteVideoBookmark(userId, bookmarkId);
    }

    @Operation(summary = "즐겨찾기 조회", description = "인증된 사용자의 즐겨찾기 목록을 조회합니다.")
    @GetMapping("/list")
    public List<VideoBookmarkResponseDTO> bookmarkList(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        String userId = userDetails.getUsername();
        return videoBookmarkService.getUserVideoBookmarks(userId);
    }

    @Operation(summary = "비디오 북마크 상태 및 총 개수 조회 (로그인 필요)", description = "특정 비디오의 북마크 여부와 총 개수를 조회합니다. 로그인된 사용자 정보 기반.")
    @GetMapping("/status/{videoId}")
    public VideoBookmarkStatusResponseDTO getVideoBookmarkStatus(
            @PathVariable String videoId,
            @AuthenticationPrincipal UserDetails userDetails) { // Optional 제거, UserDetails로 복귀
        String userId = userDetails.getUsername(); // userId는 이제 항상 존재함.
        // totalCount는 여기서도 포함하여 반환해도 되지만, Frontend에서 publicCount를 먼저 받으므로 필수는 아님.
        // 현재는 StatusResponseDTO에 totalBookmarkCount 필드가 있으므로 유지.
        return videoBookmarkService.getVideoBookmarkStatus(userId, videoId);
    }

    @Operation(summary = "비디오의 총 북마크 개수 조회 (로그인 불필요)", description = "로그인 여부와 상관없이 특정 비디오의 총 북마크 개수를 조회합니다.")
    @GetMapping("/publicCount/{videoId}") // 새로운 엔드포인트
    public long getPublicTotalBookmarkCount(@PathVariable String videoId) {
        return videoBookmarkService.getTotalBookmarkCountOnlyForVideo(videoId); // 새로운 서비스 메서드 호출
    }


}