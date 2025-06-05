package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import com.team.teamreadioserver.statistics.dto.ClickedContentDTO;
import com.team.teamreadioserver.statistics.repository.ClickLogRepository;
import com.team.teamreadioserver.video.entity.Video;
import com.team.teamreadioserver.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickAnalyticsService {

    private final ClickLogRepository clickLogRepository;
    private final BookRepository bookRepository;
    private final VideoRepository videoRepository;

    public Page<ClickedContentDTO> getClickedContentList(
            String type,
            String sort,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);


        // Step 1. 클릭 수 조회 (Object[] → DTO로 수동 매핑
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        List<Object[]> rawData = clickLogRepository.findTopClickedContentWithPaging(type, start, end, limit, offset);
        long totalCount = clickLogRepository.countTotalClickedContent(type, start, end);

        List<ClickedContentDTO> baseDtos = rawData.stream().map(row ->
                ClickedContentDTO.builder()
                        .contentId((String) row[0])
                        .contentType((String) row[1])
                        .clickCount(((Number) row[2]).longValue()) // 안전 처리
                        .title("")
                        .thumbnail("")
                        .source("")
                        .build()
        ).collect(Collectors.toList());

        // Step 2. 콘텐츠 상세 조회
        List<String> ids = baseDtos.stream().map(ClickedContentDTO::getContentId).toList();

        Map<String, Book> bookMap = "book".equals(type)
                ? bookRepository.findAllByBookIsbnIn(ids).stream().collect(Collectors.toMap(Book::getBookIsbn, b -> b))
                : Collections.emptyMap();

        Map<String, Video> videoMap = "video".equals(type)
                ? videoRepository.findAllById(ids).stream().collect(Collectors.toMap(Video::getVideoId, v -> v))
                : Collections.emptyMap();


        // Step 3. builder로 재조합된 리스트 생성
        List<ClickedContentDTO> enrichedDtos = baseDtos.stream().map(dto -> {
            if ("book".equals(dto.getContentType()) && bookMap.containsKey(dto.getContentId())) {
                Book b = bookMap.get(dto.getContentId());
                return dto.toBuilder()
                        .title(b.getBookTitle())
                        .thumbnail(b.getBookCover())
                        .source(b.getBookAuthor())
                        .build();
            } else if ("video".equals(dto.getContentType()) && videoMap.containsKey(dto.getContentId())) {
                Video v = videoMap.get(dto.getContentId());
                return dto.toBuilder()
                        .title(v.getTitle())
                        .thumbnail(v.getThumbnail())
                        .source(v.getChannelTitle())
                        .build();
            }
            return dto;
        }).toList();

        return new PageImpl<>(enrichedDtos, pageable, totalCount);

    }
}
