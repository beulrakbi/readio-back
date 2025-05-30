package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import com.team.teamreadioserver.statistics.dto.ClickedContentDTO;
import com.team.teamreadioserver.statistics.repository.ClickLogRepository;
import com.team.teamreadioserver.video.entity.Video;
import com.team.teamreadioserver.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickAnalyticsService {

    private final ClickLogRepository clickLogRepository;
    private final BookRepository bookRepository;
    private final VideoRepository videoRepository;

    public List<ClickedContentDTO> getClickedContentList(
            String type,
            String sort,
            LocalDate startDate,
            LocalDate endDate,
            Integer limit,
            Integer page,
            Integer size
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // Step 1. 클릭 수 집계
        List<Object[]> rawClickData = clickLogRepository.findTopClickedContent(type, start, end);
        Map<String, Long> clickMap = rawClickData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
        List<String> contentIds = new ArrayList<>(clickMap.keySet());

        // Step 2. 콘텐츠 상세 조회
        List<ClickedContentDTO> dtos = new ArrayList<>();
        if (type.equals("book")) {
            List<Book> books = getSortedBooks(contentIds, sort);
            for (Book b : books) {
                dtos.add(ClickedContentDTO.builder()
                        .contentId(b.getBookIsbn())
                        .contentType("book")
                        .title(b.getBookTitle())
                        .thumbnail(b.getBookCover())
                        .source(b.getBookAuthor())
                        .clickCount(clickMap.getOrDefault(b.getBookIsbn(), 0L))
                        .build());
            }
        } else if (type.equals("video")) {
            List<Video> videos = getSortedVideos(contentIds, sort);
            for (Video v : videos) {
                dtos.add(ClickedContentDTO.builder()
                        .contentId(v.getVideoId())
                        .contentType("video")
                        .title(v.getTitle())
                        .thumbnail(v.getThumbnail())
                        .source(v.getChannelTitle())
                        .clickCount(clickMap.getOrDefault(v.getVideoId(), 0L))
                        .build());
            }
        }

        // Step 3. 페이징 or limit 적용
        if (limit != null) {
            return dtos.stream().limit(limit).toList();
        } else if (page != null && size != null) {
            int from = page * size;
            int to = Math.min(from + size, dtos.size());
            return dtos.subList(from, to);
        }

        return dtos;
    }

    // 책 정렬
    private List<Book> getSortedBooks(List<String> ids, String sort) {
        List<Book> books = bookRepository.findAllByBookIsbnIn(ids);
        return books.stream()
                .sorted(getBookComparator(sort))
                .toList();
    }

    // 영상 정렬
    private List<Video> getSortedVideos(List<String> ids, String sort) {
        List<Video> videos = videoRepository.findAllById(ids);
        return videos.stream()
                .sorted(getVideoComparator(sort))
                .toList();
    }

    private Comparator<Book> getBookComparator(String sort) {
        return switch (sort) {
            case "bookmark" -> Comparator.comparing(Book::getBookmarkCount, Comparator.nullsLast(Comparator.reverseOrder()));
            case "date" -> Comparator.comparing(Book::getBookPubdate, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Book::getBookIsbn); // 안정 정렬
        };
    }

    private Comparator<Video> getVideoComparator(String sort) {
        return switch (sort) {
            case "bookmark" -> Comparator.comparing(Video::getBookmarkCount, Comparator.nullsLast(Comparator.reverseOrder()));
            case "date" -> Comparator.comparing(Video::getUploadDate, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Video::getVideoId);
        };
    }
}
