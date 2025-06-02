package com.team.teamreadioserver.bookmark.service;

import com.team.teamreadioserver.bookmark.dto.VideoBookmarkRequestDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.VideoBookmarkStatusResponseDTO;
import com.team.teamreadioserver.bookmark.entity.VideoBookmark;
import com.team.teamreadioserver.bookmark.repository.VideoBookmarkRepository;
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

    public long addVideoBookmark(String userId, VideoBookmarkRequestDTO videoBookmarkRequestDTO) {
        boolean exists = videoBookmarkRepository.existsByVideoIdAndUserId(videoBookmarkRequestDTO.getVideoId(), userId);
        if (exists) {
            throw new IllegalArgumentException("이미 이 영상을 즐겨찾기했습니다.");
        }

        VideoBookmark videoBookmark = VideoBookmark.builder()
                .videoId(videoBookmarkRequestDTO.getVideoId())
                .userId(userId)
                .build();
        videoBookmarkRepository.save(videoBookmark);

        return videoBookmarkRepository.countByVideoId(videoBookmarkRequestDTO.getVideoId());
    }

    public VideoBookmarkStatusResponseDTO deleteVideoBookmark(String userId, Integer bookmarkId) {
        VideoBookmark bookmarkToDelete = videoBookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 즐겨찾기를 찾을 수 없습니다."));

        if (!bookmarkToDelete.getUserId().equals(userId)) {
            throw new SecurityException("해당 즐겨찾기를 삭제할 권한이 없습니다.");
        }

        String videoId = bookmarkToDelete.getVideoId();
        videoBookmarkRepository.deleteById(bookmarkId);

        long totalCount = videoBookmarkRepository.countByVideoId(videoId);
        // 삭제 후에는 isBookmarked: false, bookmarkId: null 로 반환
        return new VideoBookmarkStatusResponseDTO(false, totalCount, null);
    }

    public List<VideoBookmarkResponseDTO> getUserVideoBookmarks(String userId) {
        return videoBookmarkRepository.findByUserId(userId).stream()
                .map(b -> new VideoBookmarkResponseDTO(b.getVideoId(), b.getUserId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VideoBookmarkStatusResponseDTO getVideoBookmarkStatus(String userId, String videoId) {
        // userId는 이제 항상 유효한 값이라고 가정 (SecurityConfig에서 authenticated() 처리)
        boolean userHasBookmarked = videoBookmarkRepository.existsByVideoIdAndUserId(videoId, userId);
        long totalCount = videoBookmarkRepository.countByVideoId(videoId); // totalCount는 status API에서도 제공 (프론트에서 선택적 사용)

        Integer bookmarkId = null;
        if (userHasBookmarked) {
            Optional<VideoBookmark> userBookmark = videoBookmarkRepository.findByVideoIdAndUserId(videoId, userId);
            if (userBookmark.isPresent()) {
                bookmarkId = userBookmark.get().getBookmarkId();
            }
        }
        return new VideoBookmarkStatusResponseDTO(userHasBookmarked, totalCount, bookmarkId);
    }

    // 새로 추가된 메서드: 로그인 여부와 상관없이 총 북마크 개수만 조회
    @Transactional(readOnly = true)
    public long getTotalBookmarkCountOnlyForVideo(String videoId) {
        return videoBookmarkRepository.countByVideoId(videoId);
    }


}

// 커밋용 주석