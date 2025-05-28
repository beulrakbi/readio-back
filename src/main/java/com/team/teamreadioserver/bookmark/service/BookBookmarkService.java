package com.team.teamreadioserver.bookmark.service;

import com.team.teamreadioserver.bookmark.dto.BookmarkResponseDTO;
import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookBookmarkService {

    @Autowired
    private BookBookmarkRepository bookBookmarkRepository;

    //책 등록
    public void addBookBookmark(BookmarkResponseDTO bookmarkResponseDTO) {
        boolean exists = bookBookmarkRepository.existsByBookIsbnAndUserId(bookmarkResponseDTO.getBookIsbn(), bookmarkResponseDTO.getUserId());
        if (exists) {
            throw new IllegalStateException("이미 이 책을 즐겨찾기했습니다.");
        }

        BookBookmark bookBookmark = BookBookmark.builder()
                .bookIsbn(bookmarkResponseDTO.getBookIsbn())
                .userId(bookmarkResponseDTO.getUserId())
                .build();
        bookBookmarkRepository.save(bookBookmark);
    }

    //책 삭제
    public void deleteBookBookmark(Integer bookmarkId) {
        bookBookmarkRepository.deleteById(bookmarkId);
    }

    //책 조회
    public List<BookmarkResponseDTO> getUserBookBookmarks(String userId) {
        return bookBookmarkRepository.findByUserId(userId).stream()
                .map(b -> new BookmarkResponseDTO(b.getBookIsbn(), b.getUserId()))
                .toList();
    }
}
