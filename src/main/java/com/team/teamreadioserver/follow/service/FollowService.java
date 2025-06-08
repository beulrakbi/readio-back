package com.team.teamreadioserver.follow.service;

import com.team.teamreadioserver.follow.dto.FollowRequestDTO;
import com.team.teamreadioserver.follow.dto.FollowResponseDTO;
import com.team.teamreadioserver.follow.entity.Follow;
import com.team.teamreadioserver.follow.repositoy.FollowRepository;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.entity.ProfileImg;
import com.team.teamreadioserver.profile.repository.ProfileImgRepository;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final ProfileImgRepository profileImgRepository;
    private final ModelMapper modelMapper; // DTO 변환을 위해 주입

    // 팔로우 하기
    public FollowResponseDTO follow(UserDetails userDetails, FollowRequestDTO requestDto) {
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 인증 정보가 없습니다.");
        }

        String loginId = userDetails.getUsername(); // UserDetails에서 로그인 ID 가져오기

        // ProfileRepository를 사용하여 로그인 ID로 follower의 Profile 엔티티 조회
        Profile follower = profileRepository.findByUser_UserId(loginId) // 실제 ProfileRepository 메소드명 확인 필요
                .orElseThrow(() -> new EntityNotFoundException("팔로우 하는 사용자의 프로필을 찾을 수 없습니다. 로그인 ID: " + loginId));

        long followerId = follower.getProfileId(); // Profile 엔티티에서 long 타입 ID 추출

        // --- 이제 followerId는 long 타입이므로 기존 로직과 호환됩니다 ---
        long followingId = requestDto.getFollowingProfileId();

        if (followerId == followingId) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        Profile following = profileRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 받는 사용자를 찾을 수 없습니다. ID: " + followingId));

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            log.warn("Attempted to follow already followed user. Follower: {}, Following: {}", followerId, followingId);
            throw new IllegalArgumentException("이미 팔로우하고 있는 사용자입니다.");
        }

        Follow newFollow = new Follow(follower, following);
        Follow savedFollow = followRepository.save(newFollow);

        return FollowResponseDTO.fromEntity(savedFollow, modelMapper);
    }

    // 언팔로우 하기 (요청 DTO 없이, 반환 DTO 없이)
    public void unfollow(UserDetails userDetails, Long followingId) { // 👈 UserDetails 객체 받도록 수정
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 인증 정보가 없습니다. (unfollow)");
        }

        String loginId = userDetails.getUsername();

        // 현재 로그인한 사용자(팔로워)의 Profile 조회
        Profile followerProfile = profileRepository.findByUser_UserId(loginId) // 실제 ProfileRepository 메소드명 확인
                .orElseThrow(() -> new UsernameNotFoundException("언팔로우 하는 사용자의 프로필을 찾을 수 없습니다: " + loginId));

        // 언팔로우 대상(following) Profile 조회
        Profile followingProfile = profileRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("언팔로우 대상 프로필을 찾을 수 없습니다: " + followingId));

        // DB에서 팔로우 관계 조회
        Follow followRelation = followRepository.findByFollowerAndFollowing(followerProfile, followingProfile)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다. (요청자: " + followerProfile.getProfileId() + ", 대상: " + followingId + ")"));

        followRepository.delete(followRelation);
        log.info("Unfollowed successfully. Follower: {}, Following: {}", followerProfile.getProfileId(), followingId);
    }

    /**
     * 특정 사용자가 '팔로우하는' 사람들 목록 조회
     * @param profileId 목록을 조회할 사용자의 프로필 ID
     * @param userDetails 현재 로그인한 사용자 정보 (isFollowing 상태 계산용)
     * @return 필요한 모든 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getFollowingList(Long profileId, UserDetails userDetails) {
        Profile user = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + profileId));

        Profile loggedInUserProfile = (userDetails != null)
                ? profileRepository.findByUser_UserId(userDetails.getUsername()).orElse(null)
                : null;

        List<Follow> follows = followRepository.findByFollower(user);

        return follows.stream()
                .map(follow -> {
                    Profile personInList = follow.getFollowing();

                    Optional<ProfileImg> profileImgOpt = profileImgRepository.findByProfile(personInList);
                    String imageUrl = profileImgOpt.map(ProfileImg::getSaveName).orElse(null);

                    // 추가 정보 계산
                    long followerCount = followRepository.countByFollowing(personInList);
                    boolean isFollowing = (loggedInUserProfile != null)
                            ? followRepository.existsByFollowerAndFollowing(loggedInUserProfile, personInList)
                            : false;

                    // ★★★ Builder 패턴으로 DTO를 생성합니다 ★★★
                    return ProfileResponseDTO.builder()
                            .profileId(personInList.getProfileId())
                            .penName(personInList.getPenName())
                            .imageUrl(imageUrl) // 조회한 이미지 파일 이름 설정
                            .followerCount(followerCount)
                            .isFollowing(isFollowing)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자를 '팔로우하는' 사람들 목록 조회
     * @param profileId 목록을 조회할 사용자의 프로필 ID
     * @param userDetails 현재 로그인한 사용자 정보 (isFollowing 상태 계산용)
     * @return 필요한 모든 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getFollowerList(Long profileId, UserDetails userDetails) {
        Profile user = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + profileId));

        Profile loggedInUserProfile = (userDetails != null)
                ? profileRepository.findByUser_UserId(userDetails.getUsername()).orElse(null)
                : null;

        List<Follow> follows = followRepository.findByFollowing(user);

        return follows.stream()
                .map(follow -> {
                    Profile personInList = follow.getFollower();
                    Optional<ProfileImg> profileImgOpt = profileImgRepository.findByProfile(personInList);
                    String imageUrl = profileImgOpt.map(ProfileImg::getSaveName).orElse(null);

                    long followerCount = followRepository.countByFollowing(personInList);
                    boolean isFollowing = (loggedInUserProfile != null)
                            ? followRepository.existsByFollowerAndFollowing(loggedInUserProfile, personInList)
                            : false;

                    return ProfileResponseDTO.builder()
                            .profileId(personInList.getProfileId())
                            .penName(personInList.getPenName())
                            .imageUrl(imageUrl) // 조회한 이미지 파일 이름 설정
                            .followerCount(followerCount)
                            .isFollowing(isFollowing)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // isFollowing 메서드는 boolean을 반환하므로 DTO 불필요 (이전과 동일)
    @Transactional(readOnly = true)
    public boolean isFollowing(UserDetails userDetails, Long targetUserId) {
        String loginId = userDetails.getUsername();

        // 현재 로그인한 사용자(팔로워)의 Profile 조회
        Profile followerProfile = profileRepository.findByUser_UserId(loginId) // 실제 ProfileRepository 메소드명 확인
                .orElseThrow(() -> new UsernameNotFoundException("현재 사용자의 프로필을 찾을 수 없습니다: " + loginId));

        Profile targetProfile = profileRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("확인 대상 프로필을 찾을 수 없습니다: " + targetUserId));

        // DB에서 팔로우 관계 확인 (followerProfile과 targetProfile 객체를 사용)
        return followRepository.findByFollowerAndFollowing(followerProfile, targetProfile).isPresent();
    }
}
