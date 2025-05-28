package com.team.teamreadioserver.bookmark.repository;

import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookBookmarkRepository extends JpaRepository<BookBookmark, Integer> {
    boolean existsByBookIsbnAndUserId(String bookIsbn, String userId);

    List<BookBookmark> findByUserId(String userId);
}
