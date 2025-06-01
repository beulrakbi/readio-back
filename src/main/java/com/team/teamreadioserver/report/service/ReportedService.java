package com.team.teamreadioserver.report.service;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.bookReview.repository.BookReviewRepository;
import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.report.dto.ReportedReviewDTO;
import com.team.teamreadioserver.report.entity.ReportedReview;
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
        
        for (ReportedReview reportedReview : reportedReviewList) {
            BookReview review = bookReviewRepository.findByReviewId(reportedReview.getReviewId());
            Profile profile = review.getProfile();
            ReportedReviewDTO reportedReviewDTO = getReportedReviewDTO(reportedReview, review, profile);

            result.add(reportedReviewDTO);
        }

        return result;
    }

    private static ReportedReviewDTO getReportedReviewDTO(ReportedReview reportedReview, BookReview review, Profile profile) {
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
}
