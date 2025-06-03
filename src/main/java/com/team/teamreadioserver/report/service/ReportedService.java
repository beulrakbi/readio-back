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
import java.util.Date; // Date import
import java.util.List;
import java.util.Optional; // Optional import

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
    public String hideReview(Integer reportId) // 반환 타입을 String으로 변경하여 일관성 유지
    {
        try {
            // ReportedReview를 reportId로 찾습니다. Optional로 받습니다.
            ReportedReview foundReport = reportedReviewRepository.findByReportId(reportId)
                    .orElseThrow(() -> new IllegalArgumentException("신고된 리뷰를 찾을 수 없습니다. (reportId: " + reportId + ")"));

            // BookReview는 ReportedReview 객체 내부에 포함되어 있습니다.
            // 지연 로딩일 수 있으므로, .getReviewId()를 호출하기 전에 bookReview 객체가 로드되는지 확인합니다.
            // 보통 @Transactional 메서드 내에서는 Lazy Loading 문제가 발생하지 않습니다.
            BookReview foundReview = foundReport.getBookReview(); // ✨ 수정된 부분: getBookReview() 사용 ✨

            if (foundReview == null) {
                throw new IllegalStateException("신고된 리뷰와 연결된 원본 리뷰를 찾을 수 없습니다.");
            }

            foundReview.hide();
            // bookReviewRepository.save(foundReview); // @Transactional이므로 명시적으로 save 안해도 됩니다.

            return "리뷰 숨기기 성공";
        } catch (Exception e) {
            log.error("[ReportedService] hideReview Fail: " + e.getMessage(), e); // 로그 메시지 수정
            throw e; // 예외를 다시 던져서 컨트롤러에서 처리하도록 합니다.
        }
    }


    public int allReportedReviewNum() {
        return reportedReviewRepository.findAll().size();
    }

    public List<ReportedReviewDTO> allReportedReviewWithPaging(Criteria cri) // 반환 타입 명확히 지정
    {
        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("reportId").descending());

        Page<ReportedReview> reportedReviewsPage = reportedReviewRepository.findAll(paging); // 메소드명 수정 findAllBy -> findAll
        List<ReportedReviewDTO> result = new ArrayList<>();

        for (ReportedReview reportedReview : reportedReviewsPage.getContent()) {
            // ReportedReview에서 BookReview 객체를 직접 가져옵니다.
            BookReview review = reportedReview.getBookReview();

            if (review != null) {
                // Profile 객체도 review에서 직접 가져옵니다.
                Profile profile = review.getProfile();

                ReportedReviewDTO reportedReviewDTO = getReportedReviewDTO(reportedReview, review, profile);
                result.add(reportedReviewDTO);
            } else {
                reportedReviewRepository.delete(reportedReview);
            }
        }
        return result;
    }


    public ReportedReviewDTO getReportedReview(Integer reportId) {
        Optional<ReportedReview> reportedReview = reportedReviewRepository.findByReportId(reportId);
        BookReview review = reportedReview.get().getBookReview();
        if (review != null) {
            Profile profile = profileRepository.findByProfileId(review.getProfile().getProfileId());
            ReportedReviewDTO reportedReviewDTO = this.getReportedReviewDTO(reportedReview.get(), review, profile);
            return reportedReviewDTO;
        } else {
            reportedReviewRepository.delete(reportedReview.get());
            return null;
        }
    }

    private ReportedReviewDTO getReportedReviewDTO(ReportedReview reportedReview, BookReview review, Profile
            profile) {
        ReportedReviewDTO reportedReviewDTO = new ReportedReviewDTO();

        reportedReviewDTO.setReportId(reportedReview.getReportId());
        reportedReviewDTO.setReviewId(review.getReviewId()); // review 객체에서 reviewId 가져오기

        // ✨ 중요: ReportedReview 엔티티의 userId는 '신고한' 유저의 ID를 의미해야 합니다.
        // 만약 '신고된 리뷰 작성자의 ID'를 원한다면 review.getProfile().getUser().getUserId()를 사용해야 합니다.
        // 현재 ReportedReview 엔티티에 신고한 유저 ID가 저장되어 있다면 reportedReview.getUserId()를 사용해야 합니다.
        reportedReviewDTO.setUserId(reportedReview.getUserId()); // 신고한 유저의 ID

        // reportedDate 필드가 ReportedReview 엔티티에 추가되었다고 가정
        reportedReviewDTO.setReportedDate(reportedReview.getReportedDate());

        reportedReviewDTO.setBookIsbn(review.getBookIsbn());
        reportedReviewDTO.setReviewContent(review.getReviewContent());
        reportedReviewDTO.setCreatedAt(review.getCreatedAt());
        reportedReviewDTO.setReportedCount(review.getReportedCount());
        reportedReviewDTO.setIsHidden(review.getIsHidden());
        return reportedReviewDTO;
    }

    public Object hidePost(Integer reportId) {
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


    public int allReportedPostNum() {
        return reportedPostRepository.findAll().size();
    }

    public Object allReportedPostWithPaging(Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("reportId").descending());


        Page<ReportedPost> reportedPosts = reportedPostRepository.findAllBy(paging);
        List<ReportedPost> reportedPostList = reportedPosts.getContent();
        List<ReportedPostDTO> result = new ArrayList<ReportedPostDTO>();
        System.out.println("list: " + reportedPostList);
        for (ReportedPost reportedPost : reportedPostList) {
            Post post = postRepository.findByPostId(reportedPost.getPostId());
            if (post != null) {
                Profile profile = profileRepository.findByProfileId(post.getProfile().getProfileId());
                ReportedPostDTO reportedPostDTO = this.getReportedPostDTO(reportedPost, post, profile);

                result.add(reportedPostDTO);
            } else {
                reportedPostRepository.delete(reportedPost);
            }
        }

        return result;
    }

    public ReportedPostDTO getReportedPost(Integer reportId) {
        ReportedPost reportedPost = reportedPostRepository.findByReportId(reportId);
        Post post = postRepository.findByPostId(reportedPost.getPostId());
        if (post != null) {
            Profile profile = profileRepository.findByProfileId(post.getProfile().getProfileId());
            ReportedPostDTO reportedPostDTO = this.getReportedPostDTO(reportedPost, post, profile);
            return reportedPostDTO;
        } else {
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

