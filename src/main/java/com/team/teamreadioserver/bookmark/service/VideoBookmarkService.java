package com.team.teamreadioserver.bookmark.service;

import com.team.teamreadioserver.bookmark.dto.BookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import com.team.teamreadioserver.bookmark.repository.VideoBookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoBookmarkService {
    @Autowired
    private VideoBookmarkRepository videoBookmarkRepository;

    //영상 등록
    public void addVideoBookmark(VideoBookmarkResponseDTO videoBookmarkResponseDTO) {
        boolean exists = videoBookmarkRepository.existsByVideoIdAndUserId(videoBookmarkResponseDTO.getVideoId(), videoBookmarkResponseDTO.getUserId());
        if (exists) {
            throw new IllegalArgumentException("이미 이 영상을 즐겨찾기했습니다.");
        }

        VideoBookmark videoBookmark = VideoBookmark.builder()
                .videoId(videoBookmarkResponseDTO.getVideoId())
                .userId(videoBookmarkResponseDTO.getUserId())
                .build();
        videoBookmarkRepository.save(videoBookmark);
    }

    //영상 삭제
    public void deleteVideoBookmark(Integer bookmarkId) {
        videoBookmarkRepository.deleteById(bookmarkId);
    }

    //영상 조회
    public List<VideoBookmarkResponseDTO> getUserVideoBookmarks(String userId) {
        return videoBookmarkRepository.findByUserId(userId).stream()
                .map(b -> new VideoBookmarkResponseDTO(b.getVideoId(), b.getUserId()))
                .toList();
    }
}
