package com.team.teamreadioserver.follow.controller;

import com.team.teamreadioserver.follow.dto.FollowRequestDTO;
import com.team.teamreadioserver.follow.dto.FollowResponseDTO;
import com.team.teamreadioserver.follow.service.FollowService;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/follow") // API 경로 일관성을 위해 /api/profiles/{profileId}/follow 대신 /api/follow 로 변경 고려
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    // private final ModelMapper modelMapper; // 서비스 계층에서 DTO 변환을 담당하면 컨트롤러에서는 불필요

    // 팔로우 하기
    @PostMapping
    public ResponseEntity<FollowResponseDTO> followUser(
            @RequestBody FollowRequestDTO requestDto /*, @AuthenticationPrincipal UserDetailsImpl userDetails */) {
        // Long currentUserId = userDetails.getProfile().getProfileId();
        Long currentUserId = 1L; // 임시 현재 사용자 ID
        FollowResponseDTO responseDto = followService.follow(currentUserId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); // 201 CREATED 응답
    }

    // 언팔로우 하기 (경로 변수로 팔로우 대상 ID를 받음)
    // RESTful하게는 DELETE /api/follow/{followingId} 또는 DELETE /api/follow?targetUserId={followingId} 등이 가능
    // 여기서는 이전 방식을 유지하되, 서비스는 followerId와 followingId를 받음
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followingId /*, @AuthenticationPrincipal UserDetailsImpl userDetails */) {
        // Long currentUserId = userDetails.getProfile().getProfileId();
        Long currentUserId = 1L; // 임시 현재 사용자 ID
        followService.unfollow(currentUserId, followingId);
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }

    // 특정 사용자가 팔로우하는 사람들 목록 (profileId의 사용자가 팔로우하는 사람들)
    @GetMapping("/{profileId}/following")
    public ResponseEntity<List<ProfileResponseDTO>> getFollowingList(@PathVariable Long profileId) {
        List<ProfileResponseDTO> responseDtoList = followService.getFollowingList(profileId);
        return ResponseEntity.ok(responseDtoList);
    }

    // 특정 사용자를 팔로우하는 사람들 목록 (profileId의 사용자를 팔로우하는 사람들)
    @GetMapping("/{profileId}/followers")
    public ResponseEntity<List<ProfileResponseDTO>> getFollowerList(@PathVariable Long profileId) {
        List<ProfileResponseDTO> responseDtoList = followService.getFollowerList(profileId);
        return ResponseEntity.ok(responseDtoList);
    }

    // 현재 로그인한 유저가 특정 유저를 팔로우하고 있는지 여부
    @GetMapping("/{targetUserId}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Long targetUserId /*, @AuthenticationPrincipal UserDetailsImpl userDetails */) {
        // Long currentUserId = userDetails.getProfile().getProfileId();
        Long currentUserId = 1L; // 임시
        boolean isFollowingStatus = followService.isFollowing(currentUserId, targetUserId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowingStatus));
    }
}