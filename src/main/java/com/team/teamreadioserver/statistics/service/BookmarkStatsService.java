package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import com.team.teamreadioserver.bookmark.repository.VideoBookmarkRepository;
import com.team.teamreadioserver.statistics.dto.BookmarkStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkStatsService {

    private final VideoBookmarkRepository videoBookmarkRepository;
    private final BookBookmarkRepository bookBookmarkRepository;

    public Page<BookmarkStatsDTO> getTopVideoBookmarks(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        List<Object[]> rows = videoBookmarkRepository.findTopVideoBookmarksWithPaging(limit, offset);
        long total = videoBookmarkRepository.countTotalVideoBookmarks();

        List<BookmarkStatsDTO> dtos = rows.stream().map(row ->
                new BookmarkStatsDTO(
                        (String) row[0],     // contentId (videoId)
                        (String) row[1],     // title
                        ((Number) row[2]).longValue()  // bookmarkCount
                )
        ).toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    public Page<BookmarkStatsDTO> getTopBookBookmarks(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        List<Object[]> rows = bookBookmarkRepository.findTopBookBookmarksWithPaging(limit, offset);
        long total = bookBookmarkRepository.countTotalBookBookmarks();

        List<BookmarkStatsDTO> dtos = rows.stream().map(row ->
                new BookmarkStatsDTO(
                        (String) row[0],     // contentId (bookIsbn)
                        (String) row[1],     // title
                        ((Number) row[2]).longValue()  // bookmarkCount
                )
        ).toList();

        return new PageImpl<>(dtos, pageable, total);
    }
}
