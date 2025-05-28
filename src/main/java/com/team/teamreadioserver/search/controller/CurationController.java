//package com.team.teamreadioserver.search.controller;
//
//import com.team.teamreadioserver.search.dto.CurationMovieDTO;
//import com.team.teamreadioserver.search.service.CurationService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@Tag(name = "큐레이션 API")
//@RestController
//@RequestMapping("/api/main")
//@RequiredArgsConstructor
//public class CurationController {
//
//    private final CurationService curationService;
//
//    // 1. 일반적인 "책 원작 영화" 목록 API
//    @Operation(summary = "책 원작 영화 목록 조회", description = "책을 원작으로 하는 영화/영상 큐레이션 목록 반환")
//    @GetMapping("/book-adaptation-movies")
//    public ResponseEntity<List<CurationMovieDTO>> getGeneralBookAdaptationMovies() {
//        List<CurationMovieDTO> movies = curationService.getGeneralBookAdaptationMovies();
//        if (movies.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(movies);
//    }
//
//    // 2. (선택적) 특정 책 제목과 관련된 영화 목록 API
////    @GetMapping("/related-book-movies")
////    public ResponseEntity<List<CurationMovieDTO>> getMoviesRelatedToBookTitle(@RequestParam String bookTitle) {
////        List<CurationMovieDTO> movies = curationService.getMoviesRelatedToBookTitle(bookTitle);
////        if (movies.isEmpty()) {
////            return ResponseEntity.noContent().build();
////        }
////        return ResponseEntity.ok(movies);
////    }
//
//}
