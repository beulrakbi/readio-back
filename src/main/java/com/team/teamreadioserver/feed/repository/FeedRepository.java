package com.team.teamreadioserver.feed.repository;

import com.team.teamreadioserver.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Post, Long> {

    @Query(value = """
    (
        SELECT
          'POST' AS type,
          p.post_id AS id,
          p.post_create_at AS createdAt,
          pr.profile_id AS profileId,
          u.user_id AS userId,
          pim.save_name AS profileImg,
          pr.pen_name AS userName,
          p.book_isbn AS bookIsbn,
          p.post_title AS title,
          p.post_content AS content,
          pi.saved_name AS contentImg,
          NULL AS reviewContent,
          NULL AS bookCoverUrl,
          NULL AS bookTitle,
          NULL AS bookAuthor,
          (SELECT COUNT(pl.likes_id) FROM post_likes pl WHERE pl.post_id = p.post_id) AS likesCount,
          (SELECT COUNT(prv.post_review_id) FROM post_review prv WHERE prv.post_id = p.post_id) AS reviewsCount,
          (SELECT CASE WHEN :loginUserProfileId IS NOT NULL AND EXISTS(SELECT 1 FROM post_likes pl WHERE pl.post_id = p.post_id AND pl.profile_id = :loginUserProfileId) THEN TRUE ELSE FALSE END) AS isLiked,
          (SELECT CASE WHEN :loginUserProfileId IS NOT NULL AND EXISTS(SELECT 1 FROM follow f WHERE f.follower_profile_id = :loginUserProfileId AND f.following_profile_id = pr.profile_id) THEN TRUE ELSE FALSE END) AS isFollowing
      FROM
          post p
      JOIN
          profile pr ON p.profile_id = pr.profile_id
      JOIN
          user u ON pr.user_id = u.user_id
      LEFT JOIN
          profile_img pim ON pr.profile_id = pim.profile_id
      LEFT JOIN
          post_img pi ON p.post_id = pi.post_id
      WHERE
          (:subTab = 'all' OR :subTab = 'post')
          AND (:#{#profileIds.isEmpty()} = TRUE OR pr.profile_id IN :profileIds)
          AND (:#{#bookIsbns.isEmpty()} = TRUE OR p.book_isbn IN :bookIsbns)
    )
    UNION ALL
    (
        SELECT
            'REVIEW' AS type,
            br.review_id AS id,
            br.created_at AS createdAt,
            pr.profile_id AS profileId,
            u.user_id AS userId,
            pim.save_name AS profileImg,
            pr.pen_name AS userName,
            br.book_isbn AS bookIsbn,
            NULL AS title,
            NULL AS content,
            NULL AS contentImg,
            br.review_content AS reviewContent,
            b.book_cover AS bookCoverUrl,
            b.book_title AS bookTitle,
            b.book_author AS bookAuthor,
            (SELECT COUNT(rl.likes_id) FROM book_review_likes rl WHERE rl.review_id = br.review_id) AS likesCount,
            0 AS reviewsCount,
            (SELECT CASE WHEN :loginUserProfileId IS NOT NULL AND EXISTS(SELECT 1 FROM book_review_likes rl WHERE rl.review_id = br.review_id AND rl.profile_id = :loginUserProfileId) THEN TRUE ELSE FALSE END) AS isLiked,
            (SELECT CASE WHEN :loginUserProfileId IS NOT NULL AND EXISTS(SELECT 1 FROM follow f WHERE f.follower_profile_id = :loginUserProfileId AND f.following_profile_id = pr.profile_id) THEN TRUE ELSE FALSE END) AS isFollowing
        
        FROM
            book_review br
        JOIN
            profile pr ON br.profile_id = pr.profile_id
        JOIN
                    user u ON pr.user_id = u.user_id
        LEFT JOIN
            book b ON br.book_isbn = b.book_isbn
        LEFT JOIN
            profile_img pim ON pr.profile_id = pim.profile_id
        WHERE
            (:subTab = 'all' OR :subTab = 'review')
            AND (:#{#profileIds.isEmpty()} = TRUE OR pr.profile_id IN :profileIds)
            AND (:#{#bookIsbns.isEmpty()} = TRUE OR br.book_isbn IN :bookIsbns)
    )
    ORDER BY createdAt DESC
    """,
            countQuery = """
        SELECT COUNT(*) FROM (
            (
                SELECT p.post_id
                FROM post p
                JOIN profile pr ON p.profile_id = pr.profile_id
                JOIN user u ON pr.user_id = u.user_id
                LEFT JOIN post_img pi ON p.post_id = pi.post_id
                WHERE (:subTab = 'all' OR :subTab = 'post')
                AND (:#{#profileIds.isEmpty()} = TRUE OR pr.profile_id IN :profileIds)
                AND (:#{#bookIsbns.isEmpty()} = TRUE OR p.book_isbn IN :bookIsbns)
            )
            UNION ALL
            (
                SELECT br.review_id
                FROM book_review br
                JOIN profile pr ON br.profile_id = pr.profile_id
                JOIN user u ON pr.user_id = u.user_id
                LEFT JOIN book b ON br.book_isbn = b.book_isbn
                WHERE (:subTab = 'all' OR :subTab = 'review')
                AND (:#{#profileIds.isEmpty()} = TRUE OR pr.profile_id IN :profileIds)
                AND (:#{#bookIsbns.isEmpty()} = TRUE OR br.book_isbn IN :bookIsbns)
            )
        ) AS combined_count
        """,
            nativeQuery = true)
    Page<FeedItemProjection> findCombinedFeed(
            @Param("subTab") String subTab,
            @Param("profileIds") List<Long> profileIds,
            @Param("bookIsbns") List<String> bookIsbns,
            @Param("loginUserProfileId") Long loginUserProfileId,
            Pageable pageable
    );
}