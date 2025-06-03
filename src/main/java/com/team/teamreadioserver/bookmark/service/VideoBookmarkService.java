// src/main/java/com/team/teamreadioserver/bookmark/service/VideoBookmarkService.java
package com.team.teamreadioserver.bookmark.service;

import com.team.teamreadioserver.bookmark.dto.VideoBookmarkRequestDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkStatusResponseDTO;
import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import com.team.teamreadioserver.bookmark.repository.VideoBookmarkRepository;
import com.team.teamreadioserver.video.entity.Video;
import com.team.teamreadioserver.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoBookmarkService {
    @Autowired
    private VideoBookmarkRepository videoBookmarkRepository;

    @Autowired
    private VideoRepository videoRepository;

    public long addVideoBookmark(String userId, VideoBookmarkRequestDTO videoBookmarkRequestDTO) {
        // 변경된 레포지토리 메서드 이름 사용
        boolean exists = videoBookmarkRepository.existsByVideo_VideoIdAndUserId(videoBookmarkRequestDTO.getVideoId(), userId);
        if (exists) {
            throw new IllegalArgumentException("이미 이 영상을 즐겨찾기했습니다.");
        }

        Video video = videoRepository.findById(videoBookmarkRequestDTO.getVideoId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오 ID입니다."));

        VideoBookmark videoBookmark = VideoBookmark.builder()
                .video(video) // Video 엔티티 객체 설정
                .userId(userId)
                .build();
        videoBookmarkRepository.save(videoBookmark);

        // 변경된 레포지토리 메서드 이름 사용
        return videoBookmarkRepository.countByVideo_VideoId(videoBookmarkRequestDTO.getVideoId());
    }

    public VideoBookmarkStatusResponseDTO deleteVideoBookmark(String userId, Integer bookmarkId) {
        VideoBookmark bookmarkToDelete = videoBookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 즐겨찾기를 찾을 수 없습니다."));

        if (!bookmarkToDelete.getUserId().equals(userId)) {
            throw new SecurityException("해당 즐겨찾기를 삭제할 권한이 없습니다.");
        }

        // VideoBookmark 엔티티의 video 필드를 통해 videoId에 접근
        String videoId = bookmarkToDelete.getVideo().getVideoId();

        videoBookmarkRepository.deleteById(bookmarkId);

        // 변경된 레포지토리 메서드 이름 사용
        long totalCount = videoBookmarkRepository.countByVideo_VideoId(videoId);
        return new VideoBookmarkStatusResponseDTO(false, totalCount, null);
    }

    public List<VideoBookmarkResponseDTO> getUserVideoBookmarks(String userId) {
        List<VideoBookmark> bookmarks = videoBookmarkRepository.findByUserId(userId);

        return bookmarks.stream()
                .map(bookmark -> {
                    Video video = bookmark.getVideo();
                    return VideoBookmarkResponseDTO.builder()
                            .bookmarkId(bookmark.getBookmarkId())
                            .videoId(video != null ? video.getVideoId() : null)
                            .videoTitle(video != null ? video.getTitle() : "제목 없음")
                            .channelTitle(video != null ? video.getChannelTitle() : "채널 없음")
                            .thumbnailUrl(video != null ? video.getThumbnail() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VideoBookmarkStatusResponseDTO getVideoBookmarkStatus(String userId, String videoId) {
        // 변경된 레포지토리 메서드 이름 사용
        boolean userHasBookmarked = videoBookmarkRepository.existsByVideo_VideoIdAndUserId(videoId, userId);
        // 변경된 레포지토리 메서드 이름 사용
        long totalCount = videoBookmarkRepository.countByVideo_VideoId(videoId);

        Integer bookmarkId = null;
        if (userHasBookmarked) {
            // 변경된 레포지토리 메서드 이름 사용
            Optional<VideoBookmark> userBookmark = videoBookmarkRepository.findByVideo_VideoIdAndUserId(videoId, userId);
            if (userBookmark.isPresent()) {
                bookmarkId = userBookmark.get().getBookmarkId();
            }
        }
        return new VideoBookmarkStatusResponseDTO(userHasBookmarked, totalCount, bookmarkId);
    }

    @Transactional(readOnly = true)
    public long getTotalBookmarkCountOnlyForVideo(String videoId) {
        // 변경된 레포지토리 메서드 이름 사용
        return videoBookmarkRepository.countByVideo_VideoId(videoId);
    }
}