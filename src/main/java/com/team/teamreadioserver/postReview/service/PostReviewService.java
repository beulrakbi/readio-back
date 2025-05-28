package com.team.teamreadioserver.postReview.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.dto.PostResponseDTO;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.postReview.dto.PostReviewRequestDTO;
import com.team.teamreadioserver.postReview.dto.PostReviewResponseDTO;
import com.team.teamreadioserver.postReview.entity.PostReview;
import com.team.teamreadioserver.postReview.repository.PostReviewRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertPostReview(PostReviewRequestDTO postReviewRequestDTO, Integer postId) {

        Integer profileIdFromDto = postReviewRequestDTO.getProfileId();
        Integer profileIdToUse;

        if (profileIdFromDto == null || profileIdFromDto == 0) {
            log.warn("DTO로 전달된 profileId가 없거나 유효하지 않아 임시 ID '1'로 설정합니다. (DTO profileId: {})", profileIdFromDto);
            profileIdToUse = 1; // <<--- 임시로 사용할 profileId
        } else {
            profileIdToUse = profileIdFromDto;
        }
        log.info("리뷰 등록에 사용될 최종 Profile ID: {}", profileIdToUse);

        int result = 0;

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다. id=" + postId));

            Profile profile = profileRepository.findById(Long.valueOf(profileIdToUse))
                    .orElseThrow(() -> new IllegalArgumentException("해당 프로필을 찾을 수 없습니다. id=" + postReviewRequestDTO.getProfileId()));

            PostReview postReview = modelMapper.map(postReviewRequestDTO, PostReview.class);

            postReview.setPostReviewDate(new Date());
            postReview.setPost(post);
            postReview.setProfile(profile);

            postReviewRepository.save(postReview);

            result = 1;
        } catch (IllegalArgumentException iae) {
            log.error("[postReview insert] 잘못된 인자: {}", iae.getMessage(), iae);
            throw iae;
        } catch (Exception e) {
            log.error("[postReview insert] Exception!! postId: {}, requestDTO: {}", postId, postReviewRequestDTO, e);
            // 서비스 레벨에서 처리할 수 없는 예외는 다시 던져서 롤백 및 상위에서 처리
            throw new RuntimeException("리뷰 저장 중 예상치 못한 오류가 발생했습니다.", e);
        }

        return (result > 0 ) ? "리뷰 입력 성공" : "리뷰 입력 실패";
    }

    public long selectPostReviewTotal(int postId) {
        long result = postReviewRepository.countByPostPostId(postId);

        return result;
    }

    public Object selectPostReview(Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("postReviewId"));

        Page<PostReview> result = postReviewRepository.findByPostPostId(Integer.valueOf(cri.getSearchValue()), paging);
        List<PostReview> postReviews = (List<PostReview>) result.getContent();

        return postReviews.stream().map(postReview -> modelMapper.map(postReview, PostReviewResponseDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public Object updatePostReview(PostReviewRequestDTO postReviewRequestDTO) {
        int result = 0;

        try {
            PostReview postReview = postReviewRepository.findById(postReviewRequestDTO.getPostReviewId()).get();
            postReview.setPostReviewContent(postReviewRequestDTO.getPostReviewContent());

            result = 1;
        } catch (Exception e) {
            log.error("[updatePostReview] Exception!!");
        }

        return (result > 0 ) ? "리뷰 수정 성공" : "리뷰 수정 실패";
    }

    @Transactional
    public void deletePostReview(int reviewId) {
        log.info("[deletePostReview] reviewId: {} 삭제 시도", reviewId);

        PostReview reviewToDelete = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 리뷰를 찾을 수 없습니다. id=" + reviewId));

        postReviewRepository.delete(reviewToDelete);

        log.info("[deletePostReview] reviewId: {} 삭제 완료", reviewId);
    }
}

