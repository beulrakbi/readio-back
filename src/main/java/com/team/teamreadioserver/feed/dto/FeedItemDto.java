package com.team.teamreadioserver.feed.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // 기본 생성자
public class FeedItemDto {

    // DTO 필드 선언 (이전과 동일)
    private String type;
    private Long id;
    private LocalDateTime createdAt;
    private Long profileId;
    private String profileImg;
    private String userName;
    private String bookIsbn;
    private String title;
    private String content;
    private String contentImg;
    private String reviewContent;
    private String bookCoverUrl;
    private String bookTitle;
    private String bookAuthor;
    private Long likesCount;
    private Long reviewsCount;
    private Boolean isLiked;
    private Boolean isFollowing;

    public FeedItemDto(
            String type, Long id, Timestamp createdAtRaw, Long profileId,
            String profileImg, String userName, String bookIsbn, String title, String content,
            String contentImg, String reviewContent, String bookCoverUrl, String bookTitle,
            String bookAuthor, Long likesCount, Long reviewsCount,
            Long isLikedInt,
            Long isFollowingInt
    ) {
        this.type = type;
        this.id = id;
        this.createdAt = (createdAtRaw != null) ? createdAtRaw.toLocalDateTime() : null;
        this.profileId = profileId;
        this.profileImg = profileImg;
        this.userName = userName;
        this.bookIsbn = bookIsbn;
        this.title = title;
        this.content = content;
        this.contentImg = contentImg;
        this.reviewContent = reviewContent;
        this.bookCoverUrl = bookCoverUrl;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.likesCount = likesCount;
        this.reviewsCount = reviewsCount;
        this.isLiked = (isLikedInt != null) ? (isLikedInt == 1L) : null;
        this.isFollowing = (isFollowingInt != null) ? (isFollowingInt == 1L) : null;
//        this.isLiked = (isLikedInt != null) ? (isLikedInt == 1) : null;
//        this.isFollowing = (isFollowingInt != null) ? (isFollowingInt == 1) : null;
    }
}