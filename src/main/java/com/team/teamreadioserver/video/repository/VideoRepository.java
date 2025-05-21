package com.team.teamreadioserver.video.repository;

import com.team.teamreadioserver.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {
}
