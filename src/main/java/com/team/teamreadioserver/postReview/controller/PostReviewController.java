package com.team.teamreadioserver.postReview.controller;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.common.common.PageDTO;
import com.team.teamreadioserver.common.common.PagingResponseDTO;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.postReview.dto.PostReviewRequestDTO;
import com.team.teamreadioserver.postReview.service.PostReviewService;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostReviewController {

    private final PostReviewService postReviewService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Operation(summary = "포스트 리뷰 등록 요청", description = "해당 포스트 리뷰 등록이 진행됩니다.", tags = {"PostReviewController"})
    @PostMapping("/{postId}/reviews")
    public ResponseEntity<ResponseDTO> insertPostReview(@PathVariable("postId") Integer postId,
                                                        @RequestBody PostReviewRequestDTO postReviewRequestDTO,
                                                        @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", null));
        }

        // 2. UserDetails에서 loginId 가져오기
        String loginId = userDetails.getUsername();

        // 3. loginId로 AppUser 조회
        User appUser = userRepository.findByUserId(loginId) // 또는 findByUserEmail 등 실제 사용하는 메소드명
                .orElseThrow(() -> new UsernameNotFoundException("등록된 사용자가 아닙니다: " + loginId));

        // 4. AppUser에서 userId 가져오기
        // appUser.getUserId()가 String을 반환하고, Profile의 ID가 Long이라면 변환 필요
        String userIdFromString = appUser.getUserId(); // User 엔티티의 getUserId()가 String을 반환한다고 가정

        Profile userProfile = profileRepository.findByUser_UserId(userIdFromString) //
                .orElseThrow(() -> new EntityNotFoundException("User ID " + userIdFromString + "에 해당하는 프로필을 찾을 수 없습니다."));


        System.out.println("리뷰 작성자 Profile ID: " + userProfile.getProfileId());

        // 6. 서비스 호출 시 Profile 객체 추가 전달
        //    PostReviewService의 insertPostReview 메소드 시그니처 변경 필요!
        Object serviceResult = postReviewService.insertPostReview(postReviewRequestDTO, postId, userProfile);

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "리뷰 입력 성공", serviceResult));
    }

    @Operation(summary = "포스트 리뷰 조회 요청", description = "해당 포스트에 등록된 리뷰 리스트 조회가 진행됩니다.", tags = {"ReviewController"})
    @GetMapping("/{postId}/reviews")
    public ResponseEntity<ResponseDTO> selectPostReview(@PathVariable String  postId,
                                                        @RequestParam(name = "offset", defaultValue = "1") String offset) {

        Criteria cri = new Criteria(Integer.valueOf(offset), 5);
        cri.setSearchValue(postId);
        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();

        int total = (int)postReviewService.selectPostReviewTotal(Integer.valueOf(cri.getSearchValue()));

        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));
        pagingResponseDTO.setData(postReviewService.selectPostReview(cri));

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "조회 성공", pagingResponseDTO));
    }
//    @Operation(summary = "포스트 리뷰 수정 요청", description = "리뷰 작성자의 리뷰 수정이 진행됩니다.", tags = {"ReviewController"})
//    @PutMapping("/reviews/{reviewId}")
//    public ResponseEntity<ResponseDTO> updatePostReview(@RequestBody PostReviewRequestDTO postReviewRequestDTO) {
//        return ResponseEntity
//                .ok()
//                .body(new ResponseDTO(HttpStatus.OK, "리뷰 수정 성공", postReviewService.updatePostReview(postReviewRequestDTO)));
//    }

    @Operation(summary = "포스트 리뷰 삭제 요청", description = "리뷰 작성자 또는 관리자가 리뷰를 삭제합니다.", tags = {"ReviewController"}) // swagger 태그 확인
    @DeleteMapping("/reviews/{reviewId}") // 엔드포인트 예시: /post/reviews/1
    public ResponseEntity<ResponseDTO> deletePostReview(@PathVariable int reviewId ) {

        postReviewService.deletePostReview(reviewId);

        return ResponseEntity
                .ok() // 또는 204 No Content (삭제 성공 시 내용 없음)
                .body(new ResponseDTO(HttpStatus.OK, "리뷰 삭제 성공", null));
    }


}
