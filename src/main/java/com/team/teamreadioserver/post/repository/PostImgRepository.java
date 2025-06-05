package com.team.teamreadioserver.post.repository;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.entity.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImgRepository extends JpaRepository<PostImg, Integer> {
    PostImg findByPost(Post post);

}
