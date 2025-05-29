package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoBookmarkRepository extends JpaRepository<VideoBookmark, Integer> {
    boolean existsByVideoIdAndUserId(String videoId, String userId);
    List<VideoBookmark> findByUserId(String userId);
}
