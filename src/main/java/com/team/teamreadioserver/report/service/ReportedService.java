package com.team.teamreadioserver.report.service;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.bookReview.exception.DuplicateReportException; // DuplicateReportException 임포트
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime; // LocalDateTime 임포트 추가

@Service
@AllArgsConstructor
public class ReportedService {
    private final ReportedReviewRepository reportedReviewRepository;
    private final BookReviewRepository bookReviewRepository;
    private final ProfileRepository profileRepository;
    private static final Logger log = LoggerFactory.getLogger(ReportedService.class);
    private final ReportedPostRepository reportedPostRepository;
    private final PostRepository postRepository;

    // ⭐ 새로 추가된 메서드: 리뷰 신고 (중복 신고 방지 로직 포함)
    @Transactional
    public void reportReview(Integer reviewId, String reporterUserId) { // reporterUserId는 현재 로그인된 사용자 ID
        // 1. 리뷰 존재 여부 확인
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("신고하려는 리뷰가 존재하지 않습니다. (ReviewId: " + reviewId + ")"));

        // 2. ⭐ 핵심: 이미 신고된 리뷰인지 확인 (ReportedReview 엔티티의 'userId' 필드를 사용)
        boolean alreadyReported = reportedReviewRepository.existsByBookReview_ReviewIdAndUserId(reviewId, reporterUserId);
        if (alreadyReported) {
            log.warn("이미 신고된 리뷰입니다. reviewId: {}, reporterUserId: {}", reviewId, reporterUserId);
            throw new DuplicateReportException("이미 신고한 리뷰입니다."); // 커스텀 예외 발생
        }

        // 3. 신고 기록 저장
        ReportedReview reportedReview = ReportedReview.builder()
                .bookReview(bookReview)
                .userId(reporterUserId) // ⭐ ReportedReview 엔티티의 userId 필드에 값 할당
                // reportedDate는 ReportedReview 엔티티의 @PrePersist 또는 생성자에서 자동으로 설정될 것으로 가정
                .build();
        reportedReviewRepository.save(reportedReview);

        // (선택 사항) BookReview 엔티티의 신고 횟수 증가 및 숨김 처리
        // bookReview.report(); // BookReview 엔티티에 신고 횟수를 증가시키는 메서드 (예: increaseReportCount())
        // if (bookReview.getReportedCount() >= 5) { // 5회 이상 신고 시
        //     bookReview.hide(); // 또는 bookReview.hide2();
        // }
        // bookReviewRepository.save(bookReview); // @Transactional 덕분에 자동 반영 (명시적으로 호출해도 무방)

        log.info("리뷰 신고 완료: reviewId={}, reporterUserId={}", reviewId, reporterUserId);
    }

    // ⭐ 추가된 메서드: 리뷰 삭제 시 신고 기록 삭제 (BookReviewService에서 호출될 수 있음)
    @Transactional
    public void deleteReportedReviewsByBookReviewId(Integer reviewId) {
        reportedReviewRepository.deleteAllByBookReview_ReviewId(reviewId);
        log.info("리뷰 삭제에 따른 신고 기록 삭제 완료: reviewId={}", reviewId);
    }

    @Transactional
    public String hideReview(Integer reportId) {
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

            // 원본 리뷰가 삭제되었을 경우 신고 기록도 삭제 (데이터 무결성 유지)
            if (review == null) {
                reportedReviewRepository.delete(reportedReview);
                continue; // 다음 신고 기록으로 넘어감
            }

            // Profile 객체도 review에서 직접 가져옵니다.
            Profile profile = review.getProfile();

            ReportedReviewDTO reportedReviewDTO = getReportedReviewDTO(reportedReview, review, profile);
            result.add(reportedReviewDTO);
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

        // ⭐ 중요: ReportedReview 엔티티의 userId는 '신고한' 유저의 ID를 의미해야 합니다.
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

    @Transactional
    public Object hidePost(Integer reportId) {
        int result = 0;

        try {
            ReportedPost foundReport = reportedPostRepository.findByReportId(reportId);
            Post foundPost = postRepository.findByPostId(foundReport.getPostId());
            System.out.println("잘 됐나?: " + foundPost.getPostHidden());
            foundPost.hide();
            System.out.println("잘 됐나?22 : " + foundPost.getPostHidden());
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