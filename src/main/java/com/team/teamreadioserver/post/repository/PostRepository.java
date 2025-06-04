package com.team.teamreadioserver.post.repository;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findByPostId(Integer postId);
    List<Post> findByProfile(Profile profile);
    Page<Post> findByProfile(Profile profile, Pageable pageable);
}

