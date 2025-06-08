package com.team.teamreadioserver.follow.repositoy;

import com.team.teamreadioserver.follow.entity.Follow;
import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends CrudRepository<Follow, Integer> {

    // 특정 사용자가 다른 사용자를 팔로우하고 있는지 확인
    Optional<Follow> findByFollowerAndFollowing(Profile follower, Profile following);

    // 특정 사용자가 팔로우하는 모든 사람 목록 조회 (내가 팔로우 하는 사람들)
    List<Follow> findByFollower(Profile follower);

    // 특정 사용자를 팔로우하는 모든 사람 목록 조회 (나를 팔로우 하는 사람들)
    List<Follow> findByFollowing(Profile following);

    // 팔로우/팔로잉 수 카운트 (필요하다면)
    long countByFollower(Profile follower);
    long countByFollowing(Profile following);

    // 특정 프로필이 팔로우하는 모든 팔로우 관계를 조회 (following_profile_id 포함)
    List<Follow> findByFollower_ProfileId(Long followerProfileId);

    // 특정 팔로우 관계가 존재하는지 확인 (중복 팔로우 방지 등)
    Optional<Follow> findByFollower_ProfileIdAndFollowing_ProfileId(Integer followerProfileId, Integer followingProfileId);

    boolean existsByFollowerAndFollowing(Profile follower, Profile following);
}
