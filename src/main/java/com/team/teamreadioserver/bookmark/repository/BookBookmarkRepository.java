// src/main/java/com/team/teamreadioserver/bookmark/repository/BookBookmarkRepository.java
package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // @Repository 어노테이션 임포트

import java.util.List;
import java.util.Optional;

@Repository // 스프링 빈으로 등록
public interface BookBookmarkRepository extends JpaRepository<BookBookmark, Integer> {

    // 1. 특정 책과 사용자에 대한 북마크 존재 여부 확인 (기존에 있었음)
    boolean existsByBook_BookIsbnAndUserId(String bookIsbn, String userId);

    // 2. 특정 사용자의 북마크 목록 조회 (기존에 있었음)
    List<BookBookmark> findByUserId(String userId);

    // --- 추가: 특정 책의 총 북마크 개수 조회 ---
    // Service의 getTotalBookmarkCountOnlyForBook, getBookBookmarkStatus에서 사용
    long countByBook_BookIsbn(String bookIsbn);

    // --- 추가: 특정 책에 대한 특정 사용자의 북마크 상세 조회 ---
    // Service의 getBookBookmarkStatus에서 사용
    Optional<BookBookmark> findByBook_BookIsbnAndUserId(String bookIsbn, String userId);
}