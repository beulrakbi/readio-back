package com.team.teamreadioserver.follow.controller;

import com.team.teamreadioserver.follow.dto.FollowRequestDTO;
import com.team.teamreadioserver.follow.dto.FollowResponseDTO;
import com.team.teamreadioserver.follow.service.FollowService;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> followUser(
            @RequestBody FollowRequestDTO requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Long currentUserId = userDetails.getProfile().getProfileId();
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // UserDetails 객체를 그대로 서비스로 전달
            FollowResponseDTO responseDto = followService.follow(userDetails, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 언팔로우 하기 (경로 변수로 팔로우 대상 ID를 받음)
    // RESTful하게는 DELETE /api/follow/{followingId} 또는 DELETE /api/follow?targetUserId={followingId} 등이 가능
    // 여기서는 이전 방식을 유지하되, 서비스는 followerId와 followingId를 받음
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal UserDetails userDetails) { // 👈 @AuthenticationPrincipal 사용

        if (userDetails == null) {
            // 사용자가 인증되지 않은 경우 (보통 Spring Security가 먼저 처리)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // UserDetails 객체와 언팔로우 대상 ID를 서비스로 전달
            followService.unfollow(userDetails, followingId);
            return ResponseEntity.noContent().build(); // 204 No Content 응답
        } catch (IllegalArgumentException e) {
            // 예: 팔로우 관계가 존재하지 않을 때 서비스에서 발생시킬 수 있는 예외
            // 실제로는 서비스에서 EntityNotFoundException 등을 발생시키고
            // @ControllerAdvice에서 404 등으로 변환하는 것이 더 적절할 수 있습니다.
            // 여기서는 간단히 400으로 처리하거나, 메시지를 포함한 404로 응답할 수 있습니다.
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.badRequest().build(); // 또는 구체적인 에러 응답
        } catch (EntityNotFoundException e) {
            // 사용자를 찾지 못한 경우
            return ResponseEntity.notFound().build();
        }
    }

    // 특정 사용자가 팔로우하는 사람들 목록 (profileId의 사용자가 팔로우하는 사람들)
    @GetMapping("/{profileId}/following")
    public ResponseEntity<List<ProfileResponseDTO>> getFollowingList(@PathVariable Long profileId,
                                                                     @AuthenticationPrincipal UserDetails userDetails){
        List<ProfileResponseDTO> responseDtoList = followService.getFollowingList(profileId, userDetails);
        return ResponseEntity.ok(responseDtoList);
    }

    // 특정 사용자를 팔로우하는 사람들 목록 (profileId의 사용자를 팔로우하는 사람들)
    @GetMapping("/{profileId}/followers")
    public ResponseEntity<List<ProfileResponseDTO>> getFollowerList(@PathVariable Long profileId,
                                                                    @AuthenticationPrincipal UserDetails userDetails){
        List<ProfileResponseDTO> responseDtoList = followService.getFollowerList(profileId, userDetails);
        return ResponseEntity.ok(responseDtoList);
    }

    // 현재 로그인한 유저가 특정 유저를 팔로우하고 있는지 여부
    @GetMapping("/{targetUserId}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal UserDetails userDetails) { // 👈 @AuthenticationPrincipal 사용

        if (userDetails == null) {
            // 사용자가 인증되지 않은 경우 (보통 Spring Security가 먼저 처리하지만, 방어적으로)
            // 이 API는 인증된 사용자만 호출 가능해야 하며, 그 경우 이 조건은 거의 발생 안 함
            // 만약 비인증 사용자도 호출 가능하고 그 경우 false를 반환해야 한다면 이 로직 유지
            return ResponseEntity.ok(Map.of("isFollowing", false));
        }

        // UserDetails 객체를 서비스로 전달
        boolean isFollowingStatus = followService.isFollowing(userDetails, targetUserId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowingStatus));
    }
}