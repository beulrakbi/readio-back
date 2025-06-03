package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoBookmarkRepository extends JpaRepository<VideoBookmark, Integer> {
    // EXISTS: video_bookmark.video.videoId (Video 엔티티의 videoId)와 파라미터 videoId를 비교
    boolean existsByVideo_VideoIdAndUserId(String videoId, String userId); // <--- existsByVideoIdAndUserId -> existsByVideo_VideoIdAndUserId 로 변경

    // FIND: findByUserId는 그대로 사용 가능 (VideoBookmark 엔티티의 userId 필드에 직접 접근)
    List<VideoBookmark> findByUserId(String userId);

    // COUNT: countByVideoId -> countByVideo_VideoId 로 변경
    long countByVideo_VideoId(String videoId); // <--- countByVideoId -> countByVideo_VideoId 로 변경

    // FIND_BY_VIDEOID_AND_USERID: findByVideoIdAndUserId -> findByVideo_VideoIdAndUserId 로 변경
    Optional<VideoBookmark> findByVideo_VideoIdAndUserId(String videoId, String userId); // <--- findByVideoIdAndUserId -> findByVideo_VideoIdAndUserId 로 변경
}
