package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // findById를 위해 Optional import

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findByProfile_ProfileId(Long profileId);

    List<BookReview> findByBookIsbn(String isbn);

    BookReview findByReviewId(Integer reviewId);

}