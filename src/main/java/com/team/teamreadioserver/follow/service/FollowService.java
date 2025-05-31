package com.team.teamreadioserver.follow.service;

import com.team.teamreadioserver.follow.dto.FollowRequestDTO;
import com.team.teamreadioserver.follow.dto.FollowResponseDTO;
import com.team.teamreadioserver.follow.entity.Follow;
import com.team.teamreadioserver.follow.repositoy.FollowRepository;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper; // DTO 변환을 위해 주입

    // 팔로우 하기
    public FollowResponseDTO follow(long followerId, FollowRequestDTO requestDto) {
        long followingId = requestDto.getFollowingProfileId();

        if (followerId == followingId) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 하는 사용자를 찾을 수 없습니다. ID: " + followerId));
        Profile following = profileRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 받는 사용자를 찾을 수 없습니다. ID: " + followingId));

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new IllegalArgumentException("이미 팔로우하고 있는 사용자입니다.");
        }

        Follow newFollow = new Follow(follower, following);
        Follow savedFollow = followRepository.save(newFollow);

        // Follow 엔티티를 FollowResponseDto로 변환
        return FollowResponseDTO.fromEntity(savedFollow, modelMapper);
        // 또는 직접 매핑:
        // return modelMapper.map(savedFollow, FollowResponseDto.class);
        // (이 경우 FollowResponseDto 내 ProfileSummaryDto 필드 매핑을 위해 ModelMapper 설정이 필요할 수 있음)
    }

    // 언팔로우 하기 (요청 DTO 없이, 반환 DTO 없이)
    public void unfollow(Long followerId, Long followingId) {
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("언팔로우 하는 사용자를 찾을 수 없습니다. ID: " + followerId));
        Profile following = profileRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("언팔로우 받는 사용자를 찾을 수 없습니다. ID: " + followingId));

        Follow followRelation = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다."));

        followRepository.delete(followRelation);
    }

    // 특정 사용자가 팔로우하는 사람들 목록 (서비스가 DTO 리스트 직접 반환)
    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getFollowingList(Long profileId) {
        Profile user = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + profileId));

        return followRepository.findByFollower(user).stream()
                .map(Follow::getFollowing)
                .map(profile -> modelMapper.map(profile, ProfileResponseDTO.class)) // Profile -> ProfileSummaryDto
                .collect(Collectors.toList());
    }

    // 특정 사용자를 팔로우하는 사람들 목록 (서비스가 DTO 리스트 직접 반환)
    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getFollowerList(Long profileId) {
        Profile user = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + profileId));

        return followRepository.findByFollowing(user).stream()
                .map(Follow::getFollower)
                .map(profile -> modelMapper.map(profile, ProfileResponseDTO.class)) // Profile -> ProfileSummaryDto
                .collect(Collectors.toList());
    }

    // isFollowing 메서드는 boolean을 반환하므로 DTO 불필요 (이전과 동일)
    @Transactional(readOnly = true)
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        Profile currentUser = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다. ID: " + currentUserId));
        Profile targetUser = profileRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다. ID: " + targetUserId));

        return followRepository.findByFollowerAndFollowing(currentUser, targetUser).isPresent();
    }
}
