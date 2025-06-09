package com.team.teamreadioserver.video.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.filtering.entity.Filtering;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import com.team.teamreadioserver.video.service.CurationKeywordsService;
import com.team.teamreadioserver.video.service.EmotionVideoRecommendationService;
import com.team.teamreadioserver.video.service.VideoService;
//import com.team.teamreadioserver.video.service.WeatherVideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

//    private final WeatherVideoService weatherVideoService;
    private final CurationKeywordsService curationKeywordsService;
    private final EmotionVideoRecommendationService emotionVideoRecommendationService; // 새로 만든 서비스 주입

    @Operation(summary = "비디오 등록 요청", description = "비디오가 등록됩니다.", tags = { "VideoController" })
    @PostMapping("/insert")
    public ResponseEntity<ResponseDTO> insertVideo(@RequestBody VideoDTO videoDTO)
    {
        log.info("[VideoController] insertVideo");
        System.out.println("videoDTO: " + videoDTO);
        Object result = videoService.insertVideo(videoDTO);
            System.out.println("들어감: " + videoDTO);
            return ResponseEntity.ok().body(
                    new ResponseDTO(HttpStatus.OK, "비디오 등록 성공", result)
            );
    }

    @Operation(summary = "비디오 조회", description = "비디오가 조회됩니다.", tags = { "VideoController" })
    @GetMapping("/{search}/{type}")
    public ResponseEntity<ResponseDTO> getVideoByKeyword(@PathVariable String search, @PathVariable String type)
    {
        System.out.println("search?: " + search);
        log.info("[VideoController] getVideoByKeyword");
        VideosDTO result = videoService.findVideos(search, type);
        if(result.getNum() > 0)
        {
            return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "비디오 조회 성공", result));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST, "비디오 조회 실패", null));
    }

    @Operation(summary = "비디오 검색", description = "비디오가 검색됩니다.", tags = { "VideoController" })
    @GetMapping("/query/{search}")
    public ResponseEntity<ResponseDTO> searchVideoByKeyword(
            @PathVariable String search,
            @RequestParam(name="page", defaultValue="1") int page,    // ← 추가: 페이지 번호
            @RequestParam(name="size", defaultValue="10") int size
    )
    {
        log.info("[VideoController] searchVideoByKeyword");
        VideosDTO result = videoService.searchVideos(search, page, size);
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "비디오 검색 성공", result));
    }

    // 영상 상세페이지 조회
    @Operation(summary = "해당 비디오 조회", description = "videoId로 DB 에서 비디오 정보 조회", tags = { "VideoController" })
    @GetMapping("/id/{videoId}")
    public ResponseEntity<ResponseDTO> getVideoById(@PathVariable String videoId) {
        try {
            VideoDTO videoDTO = videoService.getVideoById(videoId);
            return ResponseEntity.ok(
                    new ResponseDTO(HttpStatus.OK, "해당 비디오 조회 성공", videoDTO)
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }

    @Operation(summary = "조회수 1 증가", description = "영상 재생 시작 시 조회수 1 증가", tags = { "VideoController" })
    @PostMapping("/id/{videoId}")
    public ResponseEntity<ResponseDTO> addView(@PathVariable String videoId){
        try {
            videoService.increaseViewCount(videoId);
            return ResponseEntity.ok(
                    new ResponseDTO(HttpStatus.OK, "조회수 증가 성공", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseDTO(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }


    @Operation(summary = "날씨 기반 비디오 추천",
            description = "프론트엔드가 전달한 날씨 키워드로 DB 조회를 수행합니다. (예: keyword=맑음 도서)",
            tags = { "VideoController" })
    @GetMapping("/weather")
    public ResponseEntity<ResponseDTO> getVideosByWeatherKeyword(
            @RequestParam("keyword") String keyword
    ) {
        log.info("[VideoController] getVideosByWeatherKeyword 호출 - keyword: {}", keyword);

        // 기존 findVideos(...) 대신 새 메서드 사용
        VideosDTO videosDTO = videoService.findWeatherVideos(keyword);

        String message = videosDTO.getNum() > 0
                ? "날씨 기반 비디오 조회 성공"
                : "해당 키워드에 매칭되는 영상이 없습니다.";

        return ResponseEntity.ok(
                new ResponseDTO(HttpStatus.OK, message, videosDTO)
        );
    }

    @Operation(summary = "감정 기반 비디오 추천",
            description = "로그인한 사용자의 가장 최근 감정에 맞는 영상을 추천합니다. (userId 파라미터 필요)", // 설명 수정
            tags = { "VideoController" })
    @GetMapping("/recommendation/emotion")
    public ResponseEntity<ResponseDTO> getEmotionBasedRecommendedVideos(
            @RequestParam(name = "userId") String userId
    ) {
        // userId 파라미터가 잘 넘어왔는지 로그로 확인
        log.info("Request for emotion based recommendation for user ID: {}", userId);

        if (userId == null) { // Long 타입은 null일 수 있으므로 체크
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(HttpStatus.BAD_REQUEST, "userId 파라미터가 필요합니다.", null));
        }

        try {
            VideosDTO recommendedVideos = emotionVideoRecommendationService.recommendVideosByEmotion(userId);
            String message = (recommendedVideos != null && recommendedVideos.getNum() > 0)
                    ? "감정 기반 비디오 추천 성공"
                    : "추천할 영상이 없거나 사용자의 감정 데이터가 없습니다.";

            return ResponseEntity.ok(
                    new ResponseDTO(HttpStatus.OK, message, recommendedVideos)
            );

        } catch (IllegalArgumentException e) {
            log.warn("[VideoController] Error getting emotion based recommendations for userId {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 또는 BAD_REQUEST
                    .body(new ResponseDTO(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            log.error("[VideoController] Unexpected error getting emotion based recommendations for userId " + userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "감정 기반 추천 중 서버 오류가 발생했습니다.", null));
        }
    }

}
