package com.team.teamreadioserver.report.service;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.report.dto.ReportedPostDTO;
import com.team.teamreadioserver.report.dto.ReportedReviewDTO;
import com.team.teamreadioserver.report.entity.ReportedPost;
import com.team.teamreadioserver.report.entity.ReportedReview;
import com.team.teamreadioserver.report.repository.ReportedPostRepository;
import com.team.teamreadioserver.report.repository.ReportedReviewRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportedService {
    private final ReportedReviewRepository reportedReviewRepository;
    private final BookReviewRepository bookReviewRepository;
    private final ProfileRepository profileRepository;
    private static final Logger log = LoggerFactory.getLogger(ReportedService.class);
    private final ReportedPostRepository reportedPostRepository;
    private final PostRepository postRepository;

    @Transactional
    public Object hideReview(Integer reportId)
    {
        int result = 0;

        try {
            ReportedReview foundReport = reportedReviewRepository.findByReportId(reportId);
            BookReview foundReview = bookReviewRepository.findByReviewId(foundReport.getReviewId());
            foundReview.hide();
            result = 1;
        } catch (Exception e) {
            log.error("[VideoService] insertVideo Fail");
            throw e;
        }

        log.info("[VideoService] insertVideo End");

        return (result > 0) ? "리뷰 숨기기 성공" : "리뷰 숨기기 실패";
    }


    public int allReportedReviewNum()
    {
        return reportedReviewRepository.findAll().size();
    }

    public Object allReportedReviewWithPaging(Criteria cri)
    {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("reportId").descending());


        Page<ReportedReview> reportedReviews = reportedReviewRepository.findAllBy(paging);
        List<ReportedReview> reportedReviewList = reportedReviews.getContent();
        List<ReportedReviewDTO> result = new ArrayList<ReportedReviewDTO>();
        System.out.println("list: " + reportedReviewList);
        for (ReportedReview reportedReview : reportedReviewList) {
            BookReview review = bookReviewRepository.findByReviewId(reportedReview.getReviewId());
            if (review != null)
            {
                Profile profile = profileRepository.findByProfileId(review.getProfileId());
                ReportedReviewDTO reportedReviewDTO = this.getReportedReviewDTO(reportedReview, review, profile);

                result.add(reportedReviewDTO);
            }
            else
            {
                reportedReviewRepository.delete(reportedReview);
            }
        }

        return result;
    }

    public ReportedReviewDTO getReportedReview(Integer reportId)
    {
        ReportedReview reportedReview = reportedReviewRepository.findByReportId(reportId);
        BookReview review = bookReviewRepository.findByReviewId(reportedReview.getReviewId());
        if (review != null)
        {
            Profile profile = profileRepository.findByProfileId(review.getProfileId());
            ReportedReviewDTO reportedReviewDTO = this.getReportedReviewDTO(reportedReview, review, profile);
            return reportedReviewDTO;
        }
        else
        {
            reportedReviewRepository.delete(reportedReview);
            return null;
        }
    }

    private ReportedReviewDTO getReportedReviewDTO(ReportedReview reportedReview, BookReview review, Profile profile) {
        ReportedReviewDTO reportedReviewDTO = new ReportedReviewDTO();

        reportedReviewDTO.setReportId(reportedReview.getReportId());
        reportedReviewDTO.setReviewId(review.getReviewId());
        reportedReviewDTO.setUserId(profile.getUser().getUserId());
        reportedReviewDTO.setReportedDate(reportedReview.getReportedDate());

        reportedReviewDTO.setBookIsbn(review.getBookIsbn());
        reportedReviewDTO.setReviewContent(review.getReviewContent());
        reportedReviewDTO.setCreatedAt(review.getCreatedAt());
        reportedReviewDTO.setReportedCount(review.getReportedCount());
        reportedReviewDTO.setIsHidden(review.getIsHidden());
        return reportedReviewDTO;
    }

    public Object hidePost(Integer reportId)
    {
        int result = 0;

        try {
            ReportedPost foundReport = reportedPostRepository.findByReportId(reportId);
            Post foundPost = postRepository.findByPostId(foundReport.getPostId());
            foundPost.hide();
            result = 1;
        } catch (Exception e) {
            log.error("[ReportedService] hidePost Fail");
            throw e;
        }

        log.info("[ReportedService] hidePost End");

        return (result > 0) ? "포스트 숨기기 성공" : "포스트 숨기기 실패";
    }


    public int allReportedPostNum()
    {
        return reportedPostRepository.findAll().size();
    }

    public Object allReportedPostWithPaging(Criteria cri)
    {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("reportId").descending());


        Page<ReportedPost> reportedPosts = reportedPostRepository.findAllBy(paging);
        List<ReportedPost> reportedPostList = reportedPosts.getContent();
        List<ReportedPostDTO> result = new ArrayList<ReportedPostDTO>();
        System.out.println("list: " + reportedPostList);
        for (ReportedPost reportedPost : reportedPostList) {
            Post post = postRepository.findByPostId(reportedPost.getPostId());
            if (post != null)
            {
                Profile profile = profileRepository.findByProfileId(post.getProfile().getProfileId());
                ReportedPostDTO reportedPostDTO = this.getReportedPostDTO(reportedPost, post, profile);

                result.add(reportedPostDTO);
            }
            else
            {
                reportedPostRepository.delete(reportedPost);
            }
        }

        return result;
    }

    public ReportedPostDTO getReportedPost(Integer reportId)
    {
        ReportedPost reportedPost = reportedPostRepository.findByReportId(reportId);
        Post post = postRepository.findByPostId(reportedPost.getPostId());
        if (post != null)
        {
            Profile profile = profileRepository.findByProfileId(post.getProfile().getProfileId());
            ReportedPostDTO reportedPostDTO = this.getReportedPostDTO(reportedPost, post, profile);
            return reportedPostDTO;
        }
        else
        {
            reportedPostRepository.delete(reportedPost);
            return null;
        }
    }

    private ReportedPostDTO getReportedPostDTO(ReportedPost reportedPost, Post post, Profile profile) {
        ReportedPostDTO reportedPostDTO = new ReportedPostDTO();

        reportedPostDTO.setReportId(reportedPost.getReportId());
        reportedPostDTO.setPostId(post.getPostId());
        reportedPostDTO.setUserId(profile.getUser().getUserId());
        reportedPostDTO.setReportedDate(reportedPost.getReportedDate());

        reportedPostDTO.setBookIsbn(post.getBookIsbn());
        reportedPostDTO.setPostTitle(post.getPostTitle());
        reportedPostDTO.setPostContent(post.getPostContent());
        reportedPostDTO.setPostCreatedAt(post.getPostCreateDate());
        reportedPostDTO.setReportedCount(post.getPostReported());
        reportedPostDTO.setIsHidden(post.getPostHidden());
        return reportedPostDTO;
    }

}
