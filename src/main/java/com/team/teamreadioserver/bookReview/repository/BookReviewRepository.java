package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import com.team.teamreadioserver.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // findById를 위해 Optional import

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findByProfile_ProfileId(Long profileId);

    List<BookReview> findByBookIsbn(String isbn);

    BookReview findByReviewId(Integer reviewId);

    Page<BookReview> findAllByProfile(Profile profile, Pageable pageable);

    int countByProfile(Profile profile);
}