package com.team.teamreadioserver.post.repository;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.entity.PostLike;
import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByProfileAndPost(Profile profile, Post post);

    long countByPost(Post post);

    // profileId와 postId로 직접 조회하는 메소드도 여전히 유용할 수 있습니다.
    // Optional<PostLike> findByProfile_ProfileIdAndPost_PostId(Long profileId, Integer postId);
}