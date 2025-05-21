package com.team.teamreadioserver.video.repository;

import com.team.teamreadioserver.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface VideoRepository extends JpaRepository<Video, String> {

    Set<Video> findByDescriptionContaining(String search);
    Set<Video> findByTitleContaining(String search);
    Set<Video> findByVideoIdContaining(String videoId);
}
