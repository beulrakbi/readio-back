//package com.team.teamreadioserver.search.service;
//
//import com.team.teamreadioserver.search.dto.CurationMovieDTO;
//import com.team.teamreadioserver.search.entity.CurationKeywords;
//import com.team.teamreadioserver.search.repository.CurationRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CurationService {
//
//    private final CurationRepository curationRepository; // 리포지토리 주입
//
//    // 책 원작 영화 목록
//    public List<CurationMovieDTO> getGeneralBookAdaptationMovies() {
//        // DB에서 "책 원작 영화" 타입의 모든 CurationKeyword 엔티티를 조회
//        List<CurationKeywords> entries = curationRepository.findByType("책 원작 영화");
//
//        return entries.stream()
//                .map(CurationKeywords::getKeyword) // 각 엔티티에서 keyword 문자열을 추출
//                .map(this::parseMovieDataFromKeyword) // 기존 파싱 메소드 사용
//                .filter(dto -> dto.getMovieTitle() != null && !dto.getMovieTitle().isEmpty())
//                .collect(Collectors.toList());
//    }
//
//
//    // keyword 문자열에서 영화 제목과 URL 파싱 (구분자 '@@' 사용 가정)
//    private CurationMovieDTO parseMovieDataFromKeyword(String keyword) {
//        if (keyword == null || keyword.isEmpty()) {
//            return new CurationMovieDTO(null, null);
//        }
//        String[] parts = keyword.split("@@", 2);
//        String movieTitle = null;
//        String videoUrl = null;
//
//        if (parts.length > 0) {
//            movieTitle = parts[0].trim();
//        }
//        if (parts.length > 1) {
//            videoUrl = parts[1].trim();
//        }
//        return new CurationMovieDTO(movieTitle, videoUrl);
//    }
//}
//
