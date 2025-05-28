package com.team.teamreadioserver.post.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    @Operation(summary = "포스트 조회 요청", description = "포스트 조회가 진행됩니다.", tags = {"PostController"})
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO> getPostDetail(@PathVariable int postId) {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상품 상세정보 조회 성공", postService.getPostDetail(postId)));
    }

    @Operation(summary = "포스트 등록 요청", description = "포스트 등록이 진행됩니다.", tags = {"PostController"})
    @PostMapping("/writing")
    public ResponseEntity<ResponseDTO> createPost(@ModelAttribute PostRequestDTO postRequestDTO,
                                                  @RequestPart(value = "postImage", required = false) List<MultipartFile> multipartFile) {
        System.out.println("========= PostController - createPost 메소드 진입 =========");
        System.out.println("DTO - Title: " + postRequestDTO.getPostTitle());
        System.out.println("DTO - Content: " + postRequestDTO.getPostContent());
        System.out.println("DTO - BookIsbn: " + postRequestDTO.getBookIsbn());

        if (multipartFile != null && !multipartFile.isEmpty()) {
            System.out.println("총 " + multipartFile.size() + "개의 파일 수신됨.");
            for (MultipartFile file : multipartFile) {
                if (!file.isEmpty()) { // 각 파일이 비어있지 않은지 확인
                    System.out.println("File - Name: " + file.getOriginalFilename());
                    System.out.println("File - Size: " + file.getSize());
                    System.out.println("File - ContentType: " + file.getContentType());
                } else {
                    System.out.println("비어있는 파일 파트가 전달되었습니다.");
                }
            }
        } else {
            System.out.println("파일(multipartFile) - 없음 또는 비어있음");
        }
        System.out.println("========================================================");

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 등록 완료", postService.CreatePost(postRequestDTO, multipartFile)));
    }

    @Operation(summary = "포스트 수정 요청", description = "포스트 수정이 진행됩니다.", tags = {"PostController"})
    @PutMapping("/modify")
    public ResponseEntity<ResponseDTO> updatePost(@ModelAttribute PostRequestDTO postRequestDTO) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 수정 완료", postService.UpdatePost(postRequestDTO)));
    }

    @Operation(summary = "포스트 삭제 요청", description = "포스트 삭제가 진행됩니다.", tags = {"PostController"})
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ResponseDTO> deletePost(@PathVariable int postId) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 삭제 완료", postService.DeletePost(postId)));
    }

}
