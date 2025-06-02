package com.team.teamreadioserver.post.controller;

import com.team.teamreadioserver.post.service.PostLikeService;
import com.team.teamreadioserver.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Spring Security 사용 시
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // 간단한 응답용

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postlikeService;

    /**
     * 게시물 좋아요 API
     * POST /api/posts/{postId}/likes
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> likePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails // 현재 로그인한 사용자 정보
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // UserDetails 객체를 서비스로 전달
            boolean success = postlikeService.likePost(userDetails, postId);
            long likeCount = postlikeService.getLikeCount(postId); // 업데이트된 좋아요 수

            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "좋아요 처리되었습니다.", "likeCount", likeCount, "isLiked", true));
            } else {
                // 서비스에서 false를 반환하는 경우는 '이미 좋아요한 경우'로 가정
                return ResponseEntity.ok().body(Map.of("message", "이미 좋아요를 누른 게시물입니다.", "likeCount", likeCount, "isLiked", true));
            }
        } catch (EntityNotFoundException e) { // 서비스에서 Profile 또는 Post 조회 실패 시 발생 가능
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) { // 기타 잘못된 인자값 등
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 게시물 좋아요 취소 API
     * DELETE /api/posts/{postId}/likes
     */
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<?> unlikePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // UserDetails 객체를 서비스로 전달
            boolean success = postlikeService.unlikePost(userDetails, postId);
            long likeCount = postlikeService.getLikeCount(postId); // 업데이트된 좋아요 수

            if (success) {
                // 204 No Content 대신 200 OK와 함께 메시지 및 상태 반환도 가능
                return ResponseEntity.ok().body(Map.of("message", "좋아요가 취소되었습니다.", "likeCount", likeCount, "isLiked", false));
            } else {
                // 서비스에서 false를 반환하는 경우는 '좋아요한 기록이 없는 경우'로 가정
                return ResponseEntity.ok().body(Map.of("message", "좋아요한 기록이 없는 게시물입니다.", "likeCount", likeCount, "isLiked", false));
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 현재 사용자의 게시물 좋아요 상태 확인 API
     * GET /api/posts/{postId}/like-status
     */
    @GetMapping("/{postId}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus( // 반환 타입을 명시적으로 Map<String, Object>로 변경
                                                              @PathVariable Integer postId,
                                                              @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            // 로그인하지 않은 사용자는 항상 isLiked: false
            return ResponseEntity.ok(Map.of("postId", postId, "isLiked", false));
        }

        try {
            // UserDetails 객체를 서비스로 전달
            boolean isLiked = postlikeService.isPostLikedByUser(userDetails, postId);
            return ResponseEntity.ok(Map.of("postId", postId, "isLiked", isLiked));
        } catch (EntityNotFoundException e) { // PostId에 해당하는 게시물이 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage(), "isLiked", false));
        }
    }

    /**
     * 게시물 총 좋아요 수 조회 API
     * GET /api/posts/{postId}/likes/count
     */
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<?> getLikeCount(@PathVariable Integer postId) {
        try {
            long likeCount = postlikeService.getLikeCount(postId);
            return ResponseEntity.ok(Map.of("postId", postId, "likeCount", likeCount));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}
