package com.team.teamreadioserver.post.controller;

import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.service.PostService;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mylibrary")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*", allowCredentials = "true")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Operation(summary = "포스트 조회 요청", description = "포스트 조회가 진행됩니다.", tags = {"PostController"})
    @GetMapping("/post/{postId}")
    public ResponseEntity<ResponseDTO> getPostDetail(@PathVariable int postId) {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상품 상세정보 조회 성공", postService.getPostDetail(postId)));
    }

    @Operation(summary = "포스트 등록 요청", description = "포스트 등록이 진행됩니다.", tags = {"PostController"})
    @PostMapping("/post/writing")
    public ResponseEntity<ResponseDTO> createPost(@ModelAttribute PostRequestDTO postRequestDTO,
                                                  @RequestPart(value = "postImage", required = false) List<MultipartFile> multipartFile,
                                                  @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("========= PostController - createPost 메소드 진입 =========");
        System.out.println("DTO - Title: " + postRequestDTO.getPostTitle());
        System.out.println("DTO - Content: " + postRequestDTO.getPostContent());
        System.out.println("DTO - BookIsbn: " + postRequestDTO.getBookIsbn());

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", null));
        }

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
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", null));
        }
        String loginId = userDetails.getUsername();

        // 3. loginId를 사용해 우리 시스템의 User 엔티티 (AppUser) 조회
        com.team.teamreadioserver.user.entity.User appUser = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("등록된 사용자가 아닙니다: " + loginId));

        // 4. 조회된 AppUser 객체에서 연관된 Profile 객체 가져오기
        String userIdFromAppUser = appUser.getUserId();

        Profile userProfile = profileRepository.findByUser_UserId(userIdFromAppUser) //
                .orElseThrow(() -> new EntityNotFoundException("User ID " + userIdFromAppUser + "에 해당하는 프로필을 찾을 수 없습니다."));

        System.out.println("작성자 프로필 ID: " + userProfile.getProfileId()); // Profile 객체에서 ID 로깅
        System.out.println("========================================================");

        // 5. PostService의 CreatePost 메소드로 DTO, 파일, 그리고 조회한 Profile "객체" 전달
        Object serviceResult = postService.CreatePost(postRequestDTO, multipartFile, userProfile);

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 등록 완료", serviceResult));
    }

    @Operation(summary = "포스트 수정 요청", description = "포스트 수정이 진행됩니다.", tags = {"PostController"})
    @PutMapping("/post/modify/{postId}")
    public ResponseEntity<ResponseDTO> updatePost(@ModelAttribute PostRequestDTO postRequestDTO) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 수정 완료", postService.UpdatePost(postRequestDTO)));
    }

    @Operation(summary = "포스트 삭제 요청", description = "포스트 삭제가 진행됩니다.", tags = {"PostController"})
    @DeleteMapping("/post/delete/{postId}")
    public ResponseEntity<ResponseDTO> deletePost(@PathVariable int postId) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 삭제 완료", postService.DeletePost(postId)));
    }

    @Operation(summary = "포스트 신고 요청", description = "포스트 신고수를 1 증가시킵니다.", tags = {"PostController"})
    @PostMapping("/post/report/{postId}") // POST 요청으로 변경
    public ResponseEntity<ResponseDTO> reportPost(@PathVariable int postId) {
        System.out.println("========= PostController - reportPost 메소드 진입 =========");
        System.out.println("신고할 PostId: " + postId);

        // 서비스 계층의 메서드를 호출하여 신고수 증가 로직을 처리
        // 반환 값으로 현재 신고수 또는 성공 메시지를 받을 수 있습니다.
        Object serviceResult = postService.incrementReportCount(postId);

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "포스트 신고 완료", serviceResult));
    }

}
