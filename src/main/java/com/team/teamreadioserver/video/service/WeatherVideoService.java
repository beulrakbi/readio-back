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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherVideoService {

    private static final Logger log = LoggerFactory.getLogger(WeatherVideoService.class);

    // 기존에 있는 두 서비스를 주입받아 재사용
    private final CurationKeywordsService curationKeywordsService;
    private final VideoService videoService;

    // application.properties에 선언한 OpenWeather API 정보
    @Value("${openweathermap.api.url}")
    private String weatherApiUrl;

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;


    public VideosDTO getVideosByWeather(double lat, double lon) {
        try {
            // OpenWeather 호출해서 날씨 정보 가져오기 ---
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
            String koreanKeyword = mapMainToKoreanKeyword(mainWeather);
            if (koreanKeyword == null) {
                log.warn("WeatherVideoService - 매핑할 수 없는 날씨(main) 값: {}", mainWeather);
                return new VideosDTO(List.of(), 0);
            }
            log.info("날씨 기반 매핑 키워드 = {}", koreanKeyword);

            // CurationKeywordsService를 이용해 type_id=5(Weather)에 속한 모든 키워드를 가져옴
            List<CurationKeywordsDTO> allWeatherKeywords = curationKeywordsService.selectAllCurationTypesAndKeywords()
                    .stream()
                    .filter(curationDTO -> curationDTO.getCurationType().getTypeId() == 5)
                    .findFirst()
                    .map(CurationDTO::getCurationKeywords)
                    .orElse(java.util.Collections.emptyList());

            if (allWeatherKeywords.isEmpty()) {
                log.warn("WeatherVideoService - CurationKeywordsService에서 typeId=5에 대한 키워드를 찾을 수 없습니다.");
            }

            // “allWeatherKeywords” 리스트 중 한국어 키워드와 동일한 항목만 남기기 --
            List<CurationKeywordsDTO> matched = allWeatherKeywords.stream()
                    .filter(dto -> koreanKeyword.equals(dto.getKeyword()))
                    .collect(Collectors.toList());

            if (matched.isEmpty()) {
                log.warn("WeatherVideoService - DB 상에 해당 키워드 [{}] 가 존재하지 않습니다. (typeId=5 내에서)", koreanKeyword);
                return new VideosDTO(List.of(), 0);
            }

            String finalKeyword = matched.get(0).getKeyword();
            VideosDTO result = videoService.findVideos(finalKeyword, "Y");

            return result;


        } catch (Exception e) {
            log.error("WeatherVideoService - getVideosByWeather() 실행 중 에러 발생", e);
            return new VideosDTO(List.of(), 0);
        }
    }




    // 데이터베이스에 저장된 curation_keywords.keyword 로 변환
    private String mapMainToKoreanKeyword(String mainWeather) {
        return switch (mainWeather) {
            case "Clear" -> "맑은날";
            case "Clouds" -> "흐린날";
            case "Rain", "Drizzle", "Thunderstorm" -> "비오는날";
            case "Snow" -> "눈오는날";
            case "Mist", "Fog", "Haze", "Smoke", "Dust" -> "안개낀날";
            default -> null;
        };
    }
}