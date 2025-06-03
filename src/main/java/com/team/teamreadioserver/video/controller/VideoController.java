package com.team.teamreadioserver.video.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import com.team.teamreadioserver.video.service.CurationKeywordsService;
import com.team.teamreadioserver.video.service.EmotionVideoRecommendationService;
import com.team.teamreadioserver.video.service.VideoService;
import com.team.teamreadioserver.video.service.WeatherVideoService;
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
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

    private final WeatherVideoService weatherVideoService;
    private final CurationKeywordsService curationKeywordsService;
    private final EmotionVideoRecommendationService emotionVideoRecommendationService; // 새로 만든 서비스 주입

    @Operation(summary = "비디오 등록 요청", description = "비디오가 등록됩니다.", tags = { "VideoController" })
    @PostMapping("/insert")
    public ResponseEntity<ResponseDTO> insertVideo(@RequestBody VideoDTO videoDTO)
    {
        log.info("[VideoController] insertVideo");
//        System.out.println("videoDTO" + videoDTO);
        Object result = videoService.insertVideo(videoDTO);
        if (result.equals("비디오 추가 성공")) {
            return ResponseEntity.ok().body(
                    new ResponseDTO(HttpStatus.OK, "비디오 등록 성공", result)
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseDTO(HttpStatus.BAD_REQUEST, "비디오 등록 실패", null)
            );
        }
    }

    @Operation(summary = "비디오 조회", description = "비디오가 조회됩니다.", tags = { "VideoController" })
    @GetMapping("/{search}")
    public ResponseEntity<ResponseDTO> getVideoByKeyword(@PathVariable String search)
    {
        System.out.println("search?: " + search);
        log.info("[VideoController] getVideoByKeyword");
        VideosDTO result = videoService.findVideos(search);
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



    @Operation(summary = "날씨 기반 비디오 추천 및 전체 큐레이션 조회",
            description = "사용자 위치 (위도, 경도)를 받아 OpenWeather로 현재 날씨 조회 후, 날씨에 맞는 키워드로 영상을 추천하고, 전체 큐레이션 목록도 함께 반환합니다.",
            tags = { "VideoController" })
    @GetMapping("/weather") // 엔드포인트 이름 변경 또는 기존 /weather 수정
    public ResponseEntity<ResponseDTO> getVideosByWeatherWithCurations(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude
    ) {
        log.info("[VideoController] getVideosByWeatherWithCurations 호출 - lat: {}, lon: {}", latitude, longitude);

        // 1. 날씨 기반 비디오 추천
        VideosDTO videosDTO = weatherVideoService.getVideosByWeather(latitude, longitude);

        // 2. 전체 큐레이션 정보 조회
        // CurationKeywordsService에서 모든 큐레이션 타입과 해당 키워드를 가져오는 메서드 사용
        List<CurationDTO> allCurations = curationKeywordsService.selectAllCurationTypesAndKeywords();

        // 3. 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("weatherRecommendedVideos", videosDTO);
        responseData.put("allCurations", allCurations);

        String message;
        if (videosDTO.getNum() > 0) {
            message = "날씨 기반 비디오 추천 및 전체 큐레이션 조회 성공";
        } else {
            // 날씨 추천 영상이 없더라도 전체 큐레이션 정보는 전달
            message = "해당 날씨에 맞는 추천 영상이 없습니다. 전체 큐레이션은 조회되었습니다.";
        }

        return ResponseEntity.ok(
                new ResponseDTO(HttpStatus.OK, message, responseData)
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
        log.info("[VideoController] Request for emotion based recommendation for user ID: {}", userId);

        if (userId == null) { // Long 타입은 null일 수 있으므로 체크
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(HttpStatus.BAD_REQUEST, "userId 파라미터가 필요합니다.", null));
        }

        try {
            VideosDTO recommendedVideos = emotionVideoRecommendationService.recommendVideosByEmotion(userId);

            // 추천 결과가 있든 없든 성공(200 OK)으로 응답하고, 데이터 부분에 결과를 담아줍니다.
            // 프론트엔드에서 recommendedVideos.getNum() 등을 보고 분기 처리할 수 있도록 합니다.
            String message = (recommendedVideos != null && recommendedVideos.getNum() > 0)
                    ? "감정 기반 비디오 추천 성공"
                    : "추천할 영상이 없거나 사용자의 감정 데이터가 없습니다.";

            return ResponseEntity.ok(
                    new ResponseDTO(HttpStatus.OK, message, recommendedVideos)
            );

        } catch (IllegalArgumentException e) {
            // 예를 들어, EmotionVideoRecommendationService에서 사용자를 찾을 수 없을 때 발생
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
