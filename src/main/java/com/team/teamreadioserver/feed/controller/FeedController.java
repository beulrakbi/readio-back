package com.team.teamreadioserver.feed.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.feed.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*", allowCredentials = "true")
public class FeedController {

    private final FeedService feedService;
    // ... (필요한 경우 UserRepository, ProfileRepository 등)

    @Operation(summary = "피드 조회", description = "메인/서브 탭에 따라 피드 데이터를 조회합니다.", tags = {"FeedController"})
    @GetMapping()
    public ResponseEntity<ResponseDTO> getFeed(
            @RequestParam(defaultValue = "rec") String mainTab, // 'rec' or 'following'
            @RequestParam(defaultValue = "all") String subTab,  // 'all', 'post', 'review'
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails // 로그인 사용자 정보
    ) {
        String loginUserId = userDetails != null ? userDetails.getUsername() : null;
        String userEmotion = "happy"; // 임시
        String userInterests = "novel"; // 임시

        // Pageable 객체 생성
        PageRequest pageable = PageRequest.of(page, size);

        ResponseDTO response = feedService.getFeedItems(mainTab, subTab, loginUserId, userEmotion, userInterests, pageable);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}