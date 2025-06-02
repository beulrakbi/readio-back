package com.team.teamreadioserver.post.service;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.entity.PostLike;
import com.team.teamreadioserver.post.repository.PostLikeRepository;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;

    private Profile getProfileFromUserDetails(UserDetails userDetails) {
        String usernameOrUserId = userDetails.getUsername();
        return profileRepository.findByUser_UserId(usernameOrUserId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 프로필을 찾을 수 없습니다: " + usernameOrUserId));
    }

    /**
     * 게시물 좋아요 처리
     */
    @Transactional
    public boolean likePost(UserDetails userDetails, Integer postId) {
        // 1. Profile 엔티티 조회
        Profile profile = getProfileFromUserDetails(userDetails); // UserDetails로부터 Profile 객체 조회
        Long currentProfileId = profile.getProfileId(); // Long 타입의 profileId 추출

        // 2. Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        // 3. 이미 좋아요를 눌렀는지 확인 (Profile과 Post 객체로 조회)
        if (postLikeRepository.findByProfileAndPost(profile, post).isPresent()) {
            // log.info("Profile {} already liked post {}", profile.getProfileId(), post.getPostId());
            return false; // 이미 좋아요함
        }

        // 4. 좋아요 정보 저장
        // PostLike 엔티티에 public PostLike(Profile profile, Post post) 생성자가 있다고 가정
        PostLike newLike = new PostLike();
        newLike.setProfile(profile);
        newLike.setPost(post);

        postLikeRepository.save(newLike);
        return true; // 좋아요 성공
    }

    /**
     * 게시물 좋아요 취소 처리
     */
    @Transactional
    public boolean unlikePost(UserDetails userDetails, Integer postId) {
        Profile profile = getProfileFromUserDetails(userDetails);
        Long currentProfileId = profile.getProfileId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        Optional<PostLike> existingLike = postLikeRepository.findByProfileAndPost(profile, post);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            // log.info("Profile {} unliked post {}", currentProfileId, postId);
            return true;
        } else {
            // log.info("Profile {} had not liked post {} to unlike", currentProfileId, postId);
            return false;
        }
    }

    /**
     * 특정 사용자가 특정 게시물을 좋아요 했는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isPostLikedByUser(UserDetails userDetails, Integer postId) {
        try {
            Profile profile = getProfileFromUserDetails(userDetails);
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
            return postLikeRepository.findByProfileAndPost(profile, post).isPresent();
        } catch (EntityNotFoundException e) {
            // 사용자의 프로필을 찾을 수 없거나, 게시물을 찾을 수 없는 경우 false 반환 (또는 예외를 다시 던질 수도 있음)
            // log.warn("Error checking like status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 특정 게시물의 총 좋아요 수 조회
     */
    @Transactional(readOnly = true)
    public long getLikeCount(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        return postLikeRepository.countByPost(post);
    }
}