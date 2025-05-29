package com.team.teamreadioserver.search.repository;

import com.team.teamreadioserver.search.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    // 제목에 키워드가 포함된 도서 조회
    List<Book> findAllByBookTitleContaining(String keyword);

    // 저자에 키워드가 포함된 도서 전부 조회
    List<Book> findAllByBookAuthorContaining(String keyword);

    // DB 에 존재하는 ISBN 인지 확인
    List<Book> findAllByBookIsbnIn(List<String> isbns);

}
