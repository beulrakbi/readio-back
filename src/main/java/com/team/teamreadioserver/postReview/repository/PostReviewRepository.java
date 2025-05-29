package com.team.teamreadioserver.postReview.repository;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.postReview.entity.PostReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReviewRepository extends JpaRepository<PostReview, Integer> {

    long countByPostPostId(int postId);

    Page<PostReview> findByPostPostId(Integer postId, Pageable pageable);
}
