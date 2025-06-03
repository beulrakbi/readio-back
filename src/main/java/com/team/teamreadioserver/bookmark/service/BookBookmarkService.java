// src/main/java/com/team/teamreadioserver/bookmark/service/BookBookmarkService.java
package com.team.teamreadioserver.bookmark.service;

import com.team.teamreadioserver.bookmark.dto.BookBookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.dto.BookBookmarkRequestDTO;
import com.team.teamreadioserver.bookmark.dto.BookBookmarkStatusResponseDTO; // <-- BookBookmarkStatusResponseDTO 임포트
import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Optional 임포트
import java.util.stream.Collectors;

@Service
@Transactional
public class BookBookmarkService {

    @Autowired
    private BookBookmarkRepository bookBookmarkRepository;
    @Autowired
    private BookRepository bookRepository;

    public Integer addBookBookmark(String userId, BookBookmarkRequestDTO requestDTO) {
        // ... (기존 addBookBookmark 로직 유지) ...
        boolean exists = bookBookmarkRepository.existsByBook_BookIsbnAndUserId(requestDTO.getBookIsbn(), userId);
        if (exists) {
            throw new IllegalStateException("이미 이 책을 즐겨찾기했습니다.");
        }

        Book book = bookRepository.findById(requestDTO.getBookIsbn())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ISBN입니다."));

        BookBookmark bookBookmark = BookBookmark.builder()
                .book(book)
                .userId(userId)
                .build();
        BookBookmark savedBookmark = bookBookmarkRepository.save(bookBookmark);
        return savedBookmark.getBookmarkId();
    }

    @Transactional
    public void deleteBookBookmark(String userId, Integer bookmarkId) {
        // ... (기존 deleteBookBookmark 로직 유지) ...
        BookBookmark bookmarkToDelete = bookBookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 즐겨찾기를 찾을 수 없습니다."));

        if (!bookmarkToDelete.getUserId().equals(userId)) {
            throw new SecurityException("해당 즐겨찾기를 삭제할 권한이 없습니다.");
        }
        bookBookmarkRepository.deleteById(bookmarkId);
    }

    @Transactional(readOnly = true)
    public List<BookBookmarkResponseDTO> getUserBookBookmarks(String userId) {
        // ... (기존 getUserBookBookmarks 로직 유지) ...
        List<BookBookmark> bookmarks = bookBookmarkRepository.findByUserId(userId);

        return bookmarks.stream()
                .map(bookmark -> {
                    Book book = bookmark.getBook();
                    return BookBookmarkResponseDTO.builder()
                            .bookmarkId(bookmark.getBookmarkId())
                            .bookIsbn(book != null ? book.getBookIsbn() : null)
                            .bookTitle(book != null ? book.getBookTitle() : "제목 없음")
                            .bookAuthor(book != null ? book.getBookAuthor() : "저자 없음")
                            .bookCover(book != null ? book.getBookCover() : null)
                            .userId(bookmark.getUserId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // --- 추가: 비디오 서비스의 getVideoBookmarkStatus와 동일하게 구현 ---
    @Transactional(readOnly = true)
    public BookBookmarkStatusResponseDTO getBookBookmarkStatus(String userId, String bookIsbn) {
        // 1. 사용자가 이 책을 북마크했는지 여부 확인
        boolean userHasBookmarked = bookBookmarkRepository.existsByBook_BookIsbnAndUserId(bookIsbn, userId);

        // 2. 총 북마크 개수 가져오기
        long totalCount = bookBookmarkRepository.countByBook_BookIsbn(bookIsbn);

        Integer bookmarkId = null;
        if (userHasBookmarked) {
            // 3. 사용자가 북마크했을 경우, 해당 북마크의 ID 가져오기
            Optional<BookBookmark> userBookmark = bookBookmarkRepository.findByBook_BookIsbnAndUserId(bookIsbn, userId);
            if (userBookmark.isPresent()) {
                bookmarkId = userBookmark.get().getBookmarkId();
            }
        }
        // DTO의 필드명 (bookmarked, totalBookmarkCount, bookmarkId)과 일치하는지 확인
        return new BookBookmarkStatusResponseDTO(userHasBookmarked, totalCount, bookmarkId);
    }

    // --- 추가: 비디오 서비스의 getTotalBookmarkCountOnlyForVideo와 동일하게 구현 ---
    @Transactional(readOnly = true)
    public long getTotalBookmarkCountOnlyForBook(String bookIsbn) {
        return bookBookmarkRepository.countByBook_BookIsbn(bookIsbn);
    }
}