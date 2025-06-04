package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoBookmarkRepository extends JpaRepository<VideoBookmark, Integer> {

    // 1. 북마크 존재 여부
    boolean existsByVideo_VideoIdAndUserId(String videoId, String userId);

    // 2. 사용자별 북마크 목록
    List<VideoBookmark> findByUserId(String userId);

    // 3. 비디오 북마크 수
    long countByVideo_VideoId(String videoId);

    // 4. 특정 유저의 특정 영상 북마크
    Optional<VideoBookmark> findByVideo_VideoIdAndUserId(String videoId, String userId);

    // 5. 통계 조회용 (페이징 쿼리)
    @Query(value = """
    SELECT v.video_id AS contentId, v.title AS title, COUNT(*) AS count
    FROM bookmark_video vb
    JOIN video v ON vb.video_id = v.video_id
    GROUP BY v.video_id, v.title
    ORDER BY count DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> findTopVideoBookmarksWithPaging(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
    SELECT COUNT(DISTINCT v.video_id)
    FROM bookmark_video vb
    JOIN video v ON vb.video_id = v.video_id
""", nativeQuery = true)
    long countTotalVideoBookmarks();

}
