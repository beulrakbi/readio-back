package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoBookmarkRepository extends JpaRepository<VideoBookmark, Integer> {
    boolean existsByVideoIdAndUserId(String videoId, String userId);
    List<VideoBookmark> findByUserId(String userId);

    long countByVideoId(String videoId); // 특정 videoId에 대한 총 북마크 개수
    Optional<VideoBookmark> findByVideoIdAndUserId(String videoId, String userId); // 사용자 ID와 비디오 ID로 특정 북마크 찾기
}
