package com.team.teamreadioserver.search.repository;

import com.team.teamreadioserver.search.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    // 제목에 키워드가 포함된 도서 조회
    List<Book> findAllByBookTitleContaining(String keyword);

    // 저자에 키워드가 포함된 도서 전부 조회
    List<Book> findAllByBookAuthorContaining(String keyword);

    // DB 에 존재하는 ISBN 인지 확인
    List<Book> findAllByBookIsbnIn(List<String> isbns);

    @Modifying
    @Transactional
    @Query("UPDATE book b SET b.bookmarkCount = b.bookmarkCount + 1 WHERE b.bookIsbn = :isbn")
    void incrementBookmarkCount(@Param("isbn") String isbn);

    @Modifying
    @Transactional
    @Query("UPDATE book b SET b.bookmarkCount = b.bookmarkCount - 1 WHERE b.bookIsbn = :isbn AND b.bookmarkCount > 0")
    void decrementBookmarkCount(@Param("isbn") String isbn);


}
