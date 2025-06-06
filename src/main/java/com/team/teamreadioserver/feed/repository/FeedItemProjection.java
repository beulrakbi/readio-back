package com.team.teamreadioserver.feed.repository;

import java.sql.Timestamp;

public interface FeedItemProjection {
    String getType();
    Long getId();
    Timestamp getCreatedAt();
    Long getProfileId();
    String getProfileImg();
    String getUserName();
    String getBookIsbn();
    String getTitle();
    String getContent();
    String getContentImg();
    String getReviewContent();
    String getBookCoverUrl();
    String getBookTitle();
    String getBookAuthor();
    Long getLikesCount();
    Long getReviewsCount();
    Long getIsLiked();
    Long getIsFollowing();

}
