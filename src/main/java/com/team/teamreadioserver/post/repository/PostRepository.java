package com.team.teamreadioserver.post.repository;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findByPostId(Integer postId);
    List<Post> findByProfile(Profile profile);
    Page<Post> findByProfile(Profile profile, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.profile.profileId = :profileId AND YEAR(p.postCreateDate) = :year AND MONTH(p.postCreateDate) = :month")
    List<Post> findByProfileIdAndYearAndMonth(@Param("profileId") Long profileId,
                                              @Param("year") int year,
                                              @Param("month") int month);

}

