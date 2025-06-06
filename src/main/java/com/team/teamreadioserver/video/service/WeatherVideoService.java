package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherVideoService {

    private static final Logger log = LoggerFactory.getLogger(WeatherVideoService.class);

    private final CurationKeywordsService curationKeywordsService;
    private final VideoService videoService;

    @Value("${openweathermap.api.url}")
    private String weatherApiUrl;

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;


    public VideosDTO getVideosByWeather(double lat, double lon) {
        try {
            // OpenWeather 호출해서 날씨 정보 가져오기
            RestTemplate rt = new RestTemplate();
            String url = String.format("%s?lat=%f&lon=%f&appid=%s&lang=kr",
                    weatherApiUrl, lat, lon, weatherApiKey);

            // JSON 구조를 Map 형태로 받아옴
            @SuppressWarnings("unchecked")
            Map<String, Object> response = rt.getForObject(url, Map.class);
            if (response == null) {
                log.warn("WeatherVideoService - OpenWeather 응답이 null 입니다.");
                return new VideosDTO(List.of(), 0);
            }

            // "weather" 배열 중 첫 번째 객체에서 "main" 필드 꺼내기
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
            if (weatherList == null || weatherList.isEmpty()) {
                log.warn("WeatherVideoService - weather 배열이 비어있음.");
                return new VideosDTO(List.of(), 0);
            }

            String mainWeather = (String) weatherList.get(0).get("main");
            log.info("현재 날씨(main) = {}", mainWeather);





            // OpenWeather main 값을 한국어 키워드로 매핑
            List<String> koreanKeywords = mapMainToKoreanKeywordList(mainWeather);
            if (koreanKeywords.isEmpty()) {
                log.warn("WeatherVideoService - 매핑할 수 없는 날씨(main) 값: {}", mainWeather);
                return new VideosDTO(List.of(), 0);
            }
            log.info("날씨 기반 매핑 키워드 목록 = {}", koreanKeywords);

            // CurationKeywordsService를 이용해 type_id=5(Weather)에 속한 모든 키워드를 가져옴
            List<CurationKeywordsDTO> allWeatherKeywords = curationKeywordsService
                    .selectAllCurationTypesAndKeywords()
                    .stream()
                    .filter(curationDTO -> curationDTO.getCurationType().getTypeId() == 5)
                    .findFirst()
                    .map(CurationDTO::getCurationKeywords)
                    .orElse(Collections.emptyList());

            if (allWeatherKeywords.isEmpty()) {
                log.warn("WeatherVideoService - typeId=5에 대한 키워드를 찾을 수 없습니다.");
                return new VideosDTO(List.of(), 0);
            }

            // 4) “allWeatherKeywords” 중, koreanKeywords 리스트에 속한 것만 남기기
            List<String> matchedKeywords = allWeatherKeywords.stream()
                    .map(CurationKeywordsDTO::getKeyword)
                    .filter(koreanKeywords::contains)
                    .collect(Collectors.toList());

            if (matchedKeywords.isEmpty()) {
                log.warn("WeatherVideoService - DB 상에 해당 키워드 중 일치하는 항목이 없습니다. (typeId=5 내에서)");
                return new VideosDTO(List.of(), 0);
            }
            log.info("DB 내 일치하는 키워드: {}", matchedKeywords);

            // 5) matchedKeywords 각각으로 VideoService.findVideos를 호출하여 결과를 합치기
            // Set을 이용해서 videoId 중복을 제거한 뒤, 최종 DTO로 변환
            Set<String> seenVideoIds = new HashSet<>();
            List<VideoDTO> combinedList = new ArrayList<>();

            for (String keyword : matchedKeywords) {
                // 두 번째 인자는 큐레이션 타입 또는 기타 구분자로 사용할 수 있습니다.
                // 기존 findVideos 메서드 시그니처가 (String search, String type)이므로,
                // 필요에 따라 “Y” 같은 값을 넘기세요.
                VideosDTO partial = videoService.findVideos(keyword, "Y");

                if (partial.getVideoDTOList() != null) {
                    for (VideoDTO v : partial.getVideoDTOList()) {
                        if (!seenVideoIds.contains(v.getVideoId())) {
                            seenVideoIds.add(v.getVideoId());
                            combinedList.add(v);
                        }
                    }
                }
            }

            // 6) combinedList 결과를 “최대 N개”로 잘라서 DTO로 패킹 (예: 최대 10개만 반환)
            //    필요에 따라 페이징 처리를 넣어도 됩니다.
            int limit = 10;
            List<VideoDTO> finalVideos =
                    combinedList.size() > limit ? combinedList.subList(0, limit) : combinedList;

            return new VideosDTO(finalVideos, finalVideos.size());

        } catch (Exception e) {
            log.error("WeatherVideoService - getVideosByWeather() 실행 중 에러 발생", e);
            return new VideosDTO(List.of(), 0);
        }
    }




    /**
     * 기존 mapMainToKoreanKeyword(String) 대신,
     * 날씨(main)에 따라 “한글 키워드 목록”을 반환하도록 수정
     */
    private List<String> mapMainToKoreanKeywordList(String mainWeather) {
        return switch (mainWeather) {
            case "Clear"   -> List.of("맑은날", "활동", "햇살", "기분좋은", "운동");
            case "Clouds"  -> List.of("흐린날", "잔잔한", "여유", "차분한");
            case "Rain", "Drizzle", "Thunderstorm" -> List.of("비오는날", "우산", "감성", "차가운", "슬픈");
            case "Snow"    -> List.of("눈오는날", "포근한", "겨울", "눈사람", "크리스마스");
            case "Mist", "Fog", "Haze", "Smoke", "Dust" -> List.of("안개낀날", "집중", "신비로운", "몽환적");
            default        -> List.of();
        };
    }
}































