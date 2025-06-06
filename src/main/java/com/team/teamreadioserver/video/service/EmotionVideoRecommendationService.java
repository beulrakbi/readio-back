package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.emotion.enums.EmotionType;
import com.team.teamreadioserver.emotion.repository.EmotionRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.mapper.UserMapper;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionVideoRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(EmotionVideoRecommendationService.class);

    private final UserMapper userMapper;
    private final EmotionRepository emotionRepository;
    private final VideoService videoService;
    private final CurationKeywordsService curationKeywordsService;

    // 감정별 추천 키워드 목록
    private static final Map<EmotionType, List<String>> EMOTION_KEYWORDS_MAP = Map.of(
            EmotionType.HAPPY, List.of("자기계발", "성장", "긍정 심리학", "인생", "재미있는", "재미있는 소설"),
            EmotionType.SAD, List.of("위로", "힐링", "슬픔", "극복", "감성 소설", "감성", "마음 치유"),
            EmotionType.ANGRY, List.of("분노 조절", "스트레스 관리", "심리 치유", "분노 해소", "명상"),
            EmotionType.ANXIOUS, List.of("불안 극복", "마음 챙김", "심리 안정", "힐링 에세이", "명상"),
            EmotionType.NORMAL, List.of("인기", "오늘의", "베스트셀러", "책 리뷰", "일상 에세이")
    );

    public VideosDTO recommendVideosByEmotion(String userId) {
        User user = userMapper.findByUserId(userId);

        if (user == null) {
            log.warn("[EmotionRec] UserMapper: 사용자를 찾을 수 없습니다. ID: {}", userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId);
        }

        Optional<Emotion> latestEmotionOptional = emotionRepository.findTopByUserOrderByDateDescCreatedAtDesc(user);

        if (latestEmotionOptional.isEmpty()) {
            log.info("[EmotionRec] 사용자 ID '{}'에 대한 감정 데이터가 없습니다.", userId);
            return new VideosDTO(Collections.emptyList(), 0);
        }

        Emotion latestEmotion = latestEmotionOptional.get();
        EmotionType emotionType = latestEmotion.getEmotionType();
        log.info("[EmotionRec] 사용자 ID '{}'의 최근 감정: {}", userId, emotionType);

        // 키워드 목록 가져오기
        List<String> candidateKeywordsFromMap = EMOTION_KEYWORDS_MAP.getOrDefault(emotionType, Collections.emptyList());

        if (candidateKeywordsFromMap.isEmpty()) {
            log.info("[EmotionRec] EMOTION_KEYWORDS_MAP에 감정 타입 '{}'에 대해 정의된 후보 키워드가 없습니다.", emotionType);
            return new VideosDTO(Collections.emptyList(), 0);
        }
        log.info("[EmotionRec] MAP에서 가져온 감정 '{}'에 대한 후보 키워드: {}", emotionType, candidateKeywordsFromMap);

        // DB의 curation_keywords 테이블에서 type_id=6 (감정)에 해당하는 모든 활성화된 키워드 목록 가져오기
        List<String> dbConfiguredEmotionKeywords = curationKeywordsService.selectAllCurationTypesAndKeywords()
                .stream()
                .filter(curationDTO -> curationDTO.getCurationType().getTypeId() == 6)
                .findFirst()
                .map(CurationDTO::getCurationKeywords)
                .orElse(Collections.emptyList())
                .stream()
                .map(CurationKeywordsDTO::getKeyword)
                .collect(Collectors.toList());

        if (dbConfiguredEmotionKeywords.isEmpty()) {
            log.warn("[EmotionRec] DB(curation_keywords 테이블)에 type_id=6 (감정)으로 설정된 키워드가 없습니다. 추천될 영상이 없습니다.");
            return new VideosDTO(Collections.emptyList(), 0);
        }
        log.info("[EmotionRec] DB에 type_id=6 (감정)으로 설정된 키워드: {}", dbConfiguredEmotionKeywords);

        // 초기 추천 키워드 목록과 DB 큐레이션 키워드 목록을 비교하여, 양쪽 모두에 존재하는 키워드만 최종 사용
        List<String> finalKeywordsToSearch = candidateKeywordsFromMap.stream()
                .filter(dbConfiguredEmotionKeywords::contains)
                .distinct()
                .collect(Collectors.toList());

        if (finalKeywordsToSearch.isEmpty()) {
            log.info("[EmotionRec] 감정 타입 '{}'에 대해 EMOTION_KEYWORDS_MAP의 키워드와 DB 설정 키워드 간에 일치하는 항목이 없습니다.", emotionType);
            return new VideosDTO(Collections.emptyList(), 0);
        }
        log.info("[EmotionRec] 감정 '{}'에 대한 최종 검색 키워드 (DB 확인 후): {}", emotionType, finalKeywordsToSearch);

        //  최종 확정된 키워드들로 비디오 검색
        Set<String> uniqueVideoIds = new HashSet<>();
        List<VideoDTO> recommendedVideoDTOList = new ArrayList<>();

        for (String keyword : finalKeywordsToSearch) {
            if (keyword == null || keyword.trim().isEmpty()) {
                continue;
            }
            VideosDTO videosByKeyword = videoService.findVideos(keyword, "6");
            if (videosByKeyword != null && videosByKeyword.getVideoDTOList() != null) {
                videosByKeyword.getVideoDTOList().forEach(video -> {
                    if (video.getVideoId() != null && uniqueVideoIds.add(video.getVideoId())) {
                        recommendedVideoDTOList.add(video);
                    }
                });
            }
        }

        List<VideoDTO> finalRecommendations = recommendedVideoDTOList.stream()
                .limit(10)
                .collect(Collectors.toList());

        log.info("[EmotionRec] 감정 '{}'에 대해 DB에서 {}개의 비디오 추천: {} (사용된 키워드: {})", // 수정된 로그
                emotionType,
                finalRecommendations.size(),
                finalRecommendations.stream().map(VideoDTO::getVideoId).collect(Collectors.toList()),
                finalKeywordsToSearch);

        return new VideosDTO(finalRecommendations, finalRecommendations.size());
    }
}