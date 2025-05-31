package com.team.teamreadioserver.postReview.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.postReview.dto.PostReviewRequestDTO;
import com.team.teamreadioserver.postReview.dto.PostReviewResponseDTO;
import com.team.teamreadioserver.postReview.entity.PostReview;
import com.team.teamreadioserver.postReview.repository.PostReviewRepository;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.entity.ProfileImg;
import com.team.teamreadioserver.profile.repository.ProfileImgRepository;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

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
    private final ProfileImgRepository profileImgRepository;
    private final ModelMapper modelMapper;

    @Value("http://localhost:8080/img/profile/")
    private String imgUrl;

    @Transactional
    public Object insertPostReview(PostReviewRequestDTO postReviewRequestDTO, Integer postId, Profile authenticatedUserProfile) {

        // 1. 전달받은 authenticatedUserProfile 유효성 검증 (컨트롤러에서 이미 했겠지만, 서비스에서도 방어적 코딩)
        if (authenticatedUserProfile == null || authenticatedUserProfile.getProfileId() == null) {
            log.error("[insertPostReview] 리뷰 작성자 프로필 정보(authenticatedUserProfile)가 유효하지 않습니다. profileId: {}",
                    authenticatedUserProfile != null ? authenticatedUserProfile.getProfileId() : "null");

            throw new IllegalArgumentException("리뷰 작성자 정보를 확인할 수 없습니다. (Profile 정보 누락)");
        }
        log.info("리뷰 등록 시작 - Post ID: {}, Reviewer Profile ID: {}", postId, authenticatedUserProfile.getProfileId());

        try {
            // 2. 리뷰를 작성할 대상 게시물(Post) 조회
            Post reviewedPost = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("[insertPostReview] 리뷰 대상 게시물을 찾을 수 없습니다. postId={}", postId);
                        return new EntityNotFoundException("리뷰를 작성할 게시글을 찾을 수 없습니다: " + postId);
                    });

            // 3. PostReviewRequestDTO를 PostReview 엔티티로 매핑
            PostReview newReview = modelMapper.map(postReviewRequestDTO, PostReview.class);

            // 4. PostReview 엔티티에 주요 정보 설정
            newReview.setPost(reviewedPost);
            newReview.setProfile(authenticatedUserProfile);
            newReview.setPostReviewDate(new Date());

            // 5. PostReview 엔티티 저장
            PostReview savedReview = postReviewRepository.save(newReview);
            log.info("리뷰 저장 성공 - Review ID: {}, Post ID: {}, Reviewer Profile ID: {}",
                    savedReview.getPostReviewId(), postId, authenticatedUserProfile.getProfileId());

            // 6. 성공 응답 반환 (예: 생성된 리뷰의 ID나 간단한 DTO 반환)
            return "리뷰 입력 성공 (리뷰 ID: " + savedReview.getPostReviewId() + ")";

        } catch (EntityNotFoundException enfe) {
            log.warn("[insertPostReview] 엔티티 조회 실패: {}", enfe.getMessage());
            throw enfe;
        } catch (IllegalArgumentException iae) {
            log.warn("[insertPostReview] 잘못된 인자: {}", iae.getMessage());
            throw iae;
        } catch (Exception e) {
            log.error("[insertPostReview] 리뷰 저장 중 예상치 못한 오류 발생! Post ID: {}, RequestDTO: {}, Profile ID: {}",
                    postId, postReviewRequestDTO, authenticatedUserProfile.getProfileId(), e);
            throw new RuntimeException("리뷰 저장 중 오류가 발생했습니다. 다시 시도해주세요.", e);
        }
    }

    public long selectPostReviewTotal(int postId) {
        long result = postReviewRepository.countByPostPostId(postId);

        return result;
    }

    public List<PostReviewResponseDTO> selectPostReview(Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("postReviewId"));

        Page<PostReview> result = postReviewRepository.findByPostPostId(Integer.valueOf(cri.getSearchValue()), paging);
        List<PostReview> postReviews = result.getContent();

        return postReviews.stream().map(reviewEntity -> {
            // 1. 기본 매핑 (PostReview 엔티티 -> PostReviewResponseDTO)
            PostReviewResponseDTO reviewDTO = modelMapper.map(reviewEntity, PostReviewResponseDTO.class);

            // 2. 리뷰 작성자(Profile) 정보 가져와서 DTO에 채우기
            Profile authorProfileEntity = reviewEntity.getProfile();

            if (authorProfileEntity != null) {
                ProfileImg authorImgEntity = profileImgRepository.findByProfile(authorProfileEntity).orElse(null);
                String authorImageUrl = null;
                if (authorImgEntity != null && authorImgEntity.getSaveName() != null && !authorImgEntity.getSaveName().isEmpty()) {
                    authorImageUrl = imgUrl + authorImgEntity.getSaveName();
                    log.debug("[selectPostReview] Profile ID: {} 이미지 URL 설정: {}", authorProfileEntity.getProfileId(), authorImageUrl);
                } else {
                    log.debug("[selectPostReview] Profile ID: {} 프로필 이미지 정보 없음", authorProfileEntity.getProfileId());
                }

                // 프로필 이미지 URL 설정
                ProfileResponseDTO profileDataForReview = ProfileResponseDTO.builder()
                        .profileId(authorProfileEntity.getProfileId())
                        .penName(authorProfileEntity.getPenName()) // ✨ 닉네임 설정
                        .biography(authorProfileEntity.getBiography()) // 필요시 설정
                        .isPrivate(authorProfileEntity.getIsPrivate() != null ? authorProfileEntity.getIsPrivate().name() : null) // 필요시 설정
                        .imageUrl(authorImageUrl) // ✨ 이미지 URL 설정
                        .build();

                reviewDTO.setProfileId(profileDataForReview); // PostReviewResponseDTO의 profileId 필드에 설정
            } else {
                log.warn("[selectPostReview] Review ID: {} 에 연결된 작성자 프로필 정보가 없습니다.", reviewEntity.getPostReviewId());
            }

            return reviewDTO;
        }).collect(Collectors.toList());
    }

//    @Transactional
//    public Object updatePostReview(PostReviewRequestDTO postReviewRequestDTO) {
//        int result = 0;
//
//        try {
//            PostReview postReview = postReviewRepository.findById(postReviewRequestDTO.getPostReviewId()).get();
//            postReview.setPostReviewContent(postReviewRequestDTO.getPostReviewContent());
//
//            result = 1;
//        } catch (Exception e) {
//            log.error("[updatePostReview] Exception!!");
//        }
//
//        return (result > 0 ) ? "리뷰 수정 성공" : "리뷰 수정 실패";
//    }

    @Transactional
    public void deletePostReview(int reviewId) {
        log.info("[deletePostReview] reviewId: {} 삭제 시도", reviewId);

        PostReview reviewToDelete = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 리뷰를 찾을 수 없습니다. id=" + reviewId));

        postReviewRepository.delete(reviewToDelete);

        log.info("[deletePostReview] reviewId: {} 삭제 완료", reviewId);
    }
}

