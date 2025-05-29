package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import com.team.teamreadioserver.video.entity.Video;
import com.team.teamreadioserver.video.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertVideo(VideoDTO videoDTO) {
        int result = 0;
        try {

            String cleanText = StringEscapeUtils.unescapeHtml4(videoDTO.getTitle());
            videoDTO.setTitle(cleanText);


            Video video = new Video(videoDTO.getVideoId(), videoDTO.getTitle(), videoDTO.getChannelTitle(), videoDTO.getDescription(), videoDTO.getThumbnail());
            if (!videoRepository.existsById(videoDTO.getVideoId())) {
                videoRepository.save(video);
                result++;
            }
        } catch (Exception e) {
            log.error("[VideoService] insertVideo Fail");
            throw e;
        }

        log.info("[VideoService] insertVideo End");

        return (result > 0) ? "비디오 추가 성공" : "비디오 추가 실패";
    }

    public VideosDTO findVideos(String search) {
        Set<Video> videos = new LinkedHashSet<>();
        videos.addAll(videoRepository.findByDescriptionContaining(search));
        videos.addAll(videoRepository.findByTitleContaining(search));

        List<VideoDTO> videoDTOS = new ArrayList<>();
        videoDTOS.addAll(videos.stream().map(video -> modelMapper.map(video, VideoDTO.class)).collect(Collectors.toList()));
        Collections.shuffle(videoDTOS);

        for (VideoDTO videoDTO : videoDTOS) {
            String cleanText = StringEscapeUtils.unescapeHtml4(videoDTO.getTitle());
            videoDTO.setTitle(cleanText);
        }


        if (videoDTOS.size() > 10) {
            videoDTOS = videoDTOS.subList(0, 10);
        }

        VideosDTO result = new VideosDTO(videoDTOS, videoDTOS.size());

        return result;
    }

    public VideosDTO searchVideos(String search, int page, int size) {
        Set<Video> videos = new HashSet<>();
        videos.addAll(videoRepository.findAllByDescriptionContaining(search));
        videos.addAll(videoRepository.findAllByTitleContaining(search));

        List<VideoDTO> videoDTOS = new ArrayList<>();
        videoDTOS.addAll(videos.stream().map(video -> modelMapper.map(video, VideoDTO.class)).collect(Collectors.toList()));

        for (VideoDTO videoDTO : videoDTOS) {
            String cleanText = StringEscapeUtils.unescapeHtml4(videoDTO.getTitle());
            videoDTO.setTitle(cleanText);
        }

        int total = videoDTOS.size();

        int start = (page - 1) * size;                                    // ← 추가: 시작 인덱스
        int end = Math.min(start + size, videoDTOS.size());              // ← 추가: 끝 인덱스
        List<VideoDTO> paged = videoDTOS.subList(start, end);            // ← 추가: 리스트 잘라내기

        // DTO에 담기
        VideosDTO result = new VideosDTO(paged, total);                  // ← 수정: paged, total 사용
//        VideosDTO result = new VideosDTO(videoDTOS, videoDTOS.size());

        return result;
    }

    // 영상 상세페이지 => videoId 로 해당 비디오 조회 메서드
    @Transactional
    public VideoDTO getVideoById(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 videoId : " + videoId));
        VideoDTO videoDTO = modelMapper.map(video, VideoDTO.class);
        // HTML 이스케이프 제거 등 후처리
        videoDTO.setTitle(StringEscapeUtils.unescapeHtml4(videoDTO.getTitle()));
        return videoDTO;
    }
}