package com.team.teamreadioserver.video.repository;

import com.team.teamreadioserver.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface VideoRepository extends JpaRepository<Video, String> {

    @Query(value = "SELECT * FROM video WHERE description LIKE %:search% ORDER BY RAND() LIMIT 5", nativeQuery = true)
    Set<Video> findByDescriptionContaining(@Param("search") String search);
    @Query(value = "SELECT * FROM video WHERE title LIKE %:search% ORDER BY RAND() LIMIT 5", nativeQuery = true)
    Set<Video> findByTitleContaining(@Param("search") String search);
    Set<Video> findByVideoIdContaining(String videoId);

    Set<Video> findAllByDescriptionContaining(String search);
    Set<Video> findAllByTitleContaining(String search);
    @Query(value =
            "SELECT v.*, COUNT(bv.bookmark_id) AS bookmarkCount " +
                    "FROM video v " +
                    "LEFT JOIN bookmark_video bv ON v.video_id = bv.video_id " +
                    "WHERE v.video_id IN (:videoIds) " +
                    "GROUP BY v.video_id " +
                    "ORDER BY bookmarkCount DESC",
            nativeQuery = true)
    List<Object[]> findVideosSortedByBookmark(@Param("videoIds") List<String> videoIds);

    @Modifying
    @Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.videoId = :id")
    int incrementViewCount(@Param("id") String videoId);

}
