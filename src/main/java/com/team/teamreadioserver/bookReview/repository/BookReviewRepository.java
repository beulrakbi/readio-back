package com.team.teamreadioserver.bookReview.repository;

import com.team.teamreadioserver.bookReview.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {
    List<BookReview> findByProfileId(Integer profileId);
}
