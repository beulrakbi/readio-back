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

    // 팔로우 관계 삭제 (언팔로우 시)
    void deleteByFollowerAndFollowing(Profile follower, Profile following);
}
