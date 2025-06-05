package com.team.teamreadioserver.video.service;
import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.emotion.enums.EmotionType;
import com.team.teamreadioserver.emotion.repository.EmotionRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.mapper.UserMapper;
import com.team.teamreadioserver.video.dto.VideosDTO;
import com.team.teamreadioserver.video.dto.VideoDTO; // VideoDTO import 추가
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionVideoRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(EmotionVideoRecommendationService.class);

    private final UserMapper userMapper;
    private final EmotionRepository emotionRepository;
    private final VideoService videoService;

    // 감정별 추천 키워드 목록 (curation_keywords 테이블에 있는 내용 기반)
    private static final Map<EmotionType, List<String>> EMOTION_KEYWORDS_MAP = Map.of(
            EmotionType.HAPPY, List.of("신나는 음악", "즐거운 음악", "행복해지는 방법"),
            EmotionType.SAD, List.of("위로가 되는 이야기", "슬플 때 듣는 음악", "감성적인 영화 추천", "혼자 있고 싶을 때"),
            EmotionType.ANGRY, List.of("스트레스 해소법", "화 가라앉히는 방법", "액션 영화"),
            EmotionType.ANXIOUS, List.of("마음이 편안해지는", "불안감을 잠재우는", "ASMR", "명상"),
            EmotionType.NORMAL, List.of("일상 브이로그", "오늘의 책 추천", "오늘의 노래 추천", "흥미로운 이야기")
    );

    public VideosDTO recommendVideosByEmotion(String userId) {
        User user = userMapper.findByUserId(userId);

        if (user == null) {
            log.warn("[EmotionRec] UserMapper: User not found with id: {}", userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId);
        }


        Optional<Emotion> latestEmotionOptional = emotionRepository.findTopByUserOrderByDateDescCreatedAtDesc(user);

        if (latestEmotionOptional.isEmpty()) {
            log.info("[EmotionRec] No emotion data found for user: {}", userId);
            // 감정 데이터가 없으면 빈 목록을 반환 (프론트엔드에서 이를 기반으로 API 검색을 수행할 수 있음)
            return new VideosDTO(Collections.emptyList(), 0);
        }

        Emotion latestEmotion = latestEmotionOptional.get();
        EmotionType emotionType = latestEmotion.getEmotionType();
        log.info("[EmotionRec] Latest emotion for user {}: {}", userId, emotionType);

        // 해당 감정에 매칭되는 검색 키워드 목록 가져오기
        List<String> keywordsToSearch = EMOTION_KEYWORDS_MAP.getOrDefault(emotionType, Collections.emptyList());

        if (keywordsToSearch.isEmpty()) {
            log.info("[EmotionRec] No keywords defined for emotion type: {}", emotionType);
            // 정의된 키워드가 없으면 빈 목록 반환 (프론트엔드에서 API 검색으로 대체 가능)
            return new VideosDTO(Collections.emptyList(), 0);
        }

        log.info("[EmotionRec] Keywords to search for emotion {}: {}", emotionType, keywordsToSearch);

        // 여러 키워드로 영상을 DB에서 검색하고 결과를 합치는 과정 시작
        Set<String> uniqueVideoIds = new HashSet<>(); // VideoId 기준으로 중복 체크를 위한 Set (VideoDTO getVideoId() 사용)
        List<VideoDTO> recommendedVideoDTOList = new ArrayList<>(); // 최종 추천될 VideoDTO 리스트

        for (String keyword : keywordsToSearch) {
            if (keyword == null || keyword.trim().isEmpty()) {
                continue;
            }
            // VideoService를 통해 DB에서 키워드 기반 영상 검색
            VideosDTO videosByKeyword = videoService.findVideos(keyword);
            if (videosByKeyword != null && videosByKeyword.getVideoDTOList() != null) {
                videosByKeyword.getVideoDTOList().forEach(video -> {
                    // videoId를 기준으로 중복되지 않은 영상만 추가
                    if (video.getVideoId() != null && uniqueVideoIds.add(video.getVideoId())) {
                        recommendedVideoDTOList.add(video);
                    }
                });
            }
        }

        // DB에서 검색된 추천 영상 개수 제한 (예: 최대 10개)
        // 이 목록은 프론트엔드에서 YouTube API를 통해 추가 영상을 가져오기 전의 초기 목록임
        List<VideoDTO> finalRecommendations = recommendedVideoDTOList.stream()
                .limit(10) // DB 검색 결과 최대 10개로 제한
                .collect(Collectors.toList());

        log.info("[EmotionRec] Recommended {} videos from DB for emotion {}: {}", finalRecommendations.size(), emotionType, finalRecommendations.stream().map(VideoDTO::getVideoId).collect(Collectors.toList()));

        // 최종적으로 DB에서 찾은 영상 목록과 개수를 VideosDTO에 담아 반환
        // 프론트엔드는 이 정보를 바탕으로 필요시 getNewVideos를 호출하여 API로부터 추가 영상을 가져옴
        return new VideosDTO(finalRecommendations, finalRecommendations.size());
    }
}