package com.team.teamreadioserver.postReview.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.dto.PostResponseDTO;
import com.team.teamreadioserver.postReview.dto.PostReviewRequestDTO;
import com.team.teamreadioserver.postReview.entity.PostReview;
import com.team.teamreadioserver.postReview.repository.PostReviewRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertPostReview(PostReviewRequestDTO postReviewRequestDTO) {

        int result = 0;

        java.util.Date now = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String reviewDate = formatter.format(now);
        postReviewRequestDTO.setPostReviewDate(reviewDate);

        try {
            PostReview postReview = modelMapper.map(postReviewRequestDTO, PostReview.class);

            postReviewRepository.save(postReview);

            result = 1;
        } catch (Exception e) {
            log.error("[postReview insert] Exception!!");
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
        Pageable paging = PageRequest.of(index, count, Sort.by("reviewCode"));

        Page<PostReview> result = postReviewRepository.findByPostPostId(Integer.valueOf(cri.getSearchValue()), paging);
        List<PostReview> postReviews = (List<PostReview>) result.getContent();

        return postReviews.stream().map(postReview -> modelMapper.map(postReview, PostResponseDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public Object updatePostReview(PostReviewRequestDTO postReviewRequestDTO) {
        int result = 0;

        try {
            PostReview postReview = postReviewRepository.findById(postReviewRequestDTO.getPostReviewId()).get();
            postReview.setPostReviewContent(postReviewRequestDTO.getPostReivewContent());

            result = 1;
        } catch (Exception e) {
            log.error("[updatePostReview] Exception!!");
        }

        return (result > 0 ) ? "리뷰 수정 성공" : "리뷰 수정 실패";
    }

}
