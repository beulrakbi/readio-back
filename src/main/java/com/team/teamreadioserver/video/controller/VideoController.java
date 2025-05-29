package com.team.teamreadioserver.video.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.video.dto.VideoDTO;
import com.team.teamreadioserver.video.dto.VideosDTO;
import com.team.teamreadioserver.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/video")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

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

    // 영상 상세페이지
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

}
