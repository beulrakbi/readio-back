package com.team.teamreadioserver.video.service;

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

    /**
     * 1) 위도(lat), 경도(lon)를 받아 OpenWeather API 호출
     * 2) 응답으로 받은 JSON에서 "weather[0].main" 필드를 꺼내 실제 날씨(main)를 얻음
     * 3) 날씨(main) → 한국어 키워드(예: "Clear" → "맑은날") 로 매핑
     * 4) curation_keywords 테이블에서 type_id=5(Weather) 에 해당하는 키워드를 모두 불러와서
     *    실제 매핑된 한국어 키워드와 일치하는 항목을 찾음
     * 5) 찾은 키워드로 VideoService.findVideos(...) 또는 searchVideos(...) 호출하여
     *    추천할 영상을 조회 → 반환
     */
    public VideosDTO getVideosByWeather(double lat, double lon) {
        try {
            // --- 1) OpenWeather 호출해서 날씨 정보 가져오기 ---
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
            String mainWeather = (String) weatherList.get(0).get("main");  // 예: "Clear", "Clouds", "Rain", "Snow", "Mist" 등
            log.info("현재 날씨(main) = {}", mainWeather);

            // --- 2) OpenWeather main 값을 한국어 키워드로 매핑 ---
            String koreanKeyword = mapMainToKoreanKeyword(mainWeather);
            if (koreanKeyword == null) {
                log.warn("WeatherVideoService - 매핑할 수 없는 날씨(main) 값: {}", mainWeather);
                return new VideosDTO(List.of(), 0);
            }
            log.info("날씨 기반 매핑 키워드 = {}", koreanKeyword);

            // --- 3) CurationKeywordsService를 이용해 type_id=5(Weather)에 속한 모든 키워드를 가져옴 ---
//            List<CurationKeywordsDTO> allWeatherKeywords = curationKeywordsService.selectCurationKeywordsByTypeId(5);
            List<CurationKeywordsDTO> allWeatherKeywords = curationKeywordsService.selectAllKeywordsByTypeId(5);
            // selectCurationKeywordsByTypeId(5)는 내부에서 랜덤하게 최대 5개만 잘라 리턴하므로,
            // “전체” 리스트가 필요하다면 Repository 직접 호출하거나 별도로 메서드를 추가해야 할 수도 있음.
            // 다만 여기서는 type_id가 고정된 “Weather”이므로,
            // 간편하게도 DB 쿼리를 직접 꺼내는 방법이나, CurationKeywordsService에 전체 조회 메서드를 추가하는 방법이 있습니다.
            // 예시에서는 “전체” 대신 “selectAllCurationTypesAndKeywords” 에서 CurationDTO를 받아서 필터링해도 됩니다.
            //
            // 간단히, 전체 키워드를 뽑아오려면 Repository를 바로 사용해도 되는데,
            // 여기서는 “최초에 저장된 DB 화씨 5개만 리턴된다”는 점을 주의하세요.
            //
            // ▼ 예시: “모든 키워드”를 꺼내고 싶다면 Repository를 직접 쓰는 메서드를 아래처럼 CurationKeywordsService에 추가하고,
            // 해당 메서드를 호출하셔야 합니다.
            //
            // List<CurationKeywordsDTO> allWeatherKeywords = curationKeywordsService.selectAllKeywordsByTypeId(5);
            //

            // -- 4) “allWeatherKeywords” 리스트 중 한국어 키워드와 동일한 항목만 남기기 --
            List<CurationKeywordsDTO> matched = allWeatherKeywords.stream()
                    .filter(dto -> koreanKeyword.equals(dto.getKeyword()))
                    .collect(Collectors.toList());

            if (matched.isEmpty()) {
                log.warn("WeatherVideoService - DB 상에 해당 키워드 [{}] 가 존재하지 않습니다.", koreanKeyword);
                return new VideosDTO(List.of(), 0);
            }

            // 이번 예시에서는 matched 리스트가 중복되지 않으므로, 첫 번째 요소의 keyword를 써도 무방
            String finalKeyword = matched.get(0).getKeyword();

            // --- 5) VideoService를 이용해 해당 키워드로 영상 조회 (findVideos 또는 searchVideos 중 선택) ---
            // 여기서 findVideos(String search)는 description/title에 포함된 영상을 중복 제거 후 최대 10개 뽑는 메서드입니다.
            VideosDTO result = videoService.findVideos(finalKeyword);

            return result;

        } catch (Exception e) {
            log.error("WeatherVideoService - getVideosByWeather() 실행 중 에러 발생", e);
            return new VideosDTO(List.of(), 0);
        }
    }

    /**
     * OpenWeather의 “main” 값을 한국어 키워드(데이터베이스에 저장된 curation_keywords.keyword)로 변환
     */
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