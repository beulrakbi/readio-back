package com.team.teamreadioserver.feed.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import com.team.teamreadioserver.common.common.ResponseDTO;
import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.emotion.enums.EmotionType;
import com.team.teamreadioserver.emotion.repository.EmotionRepository;
import com.team.teamreadioserver.feed.dto.FeedItemDto;
import com.team.teamreadioserver.feed.dto.FeedResponseDto;
import com.team.teamreadioserver.feed.repository.FeedRepository;
import com.team.teamreadioserver.follow.repositoy.FollowRepository;
import com.team.teamreadioserver.interest.entity.InterestCategory;

import com.team.teamreadioserver.interest.entity.InterestKeyword;
import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import com.team.teamreadioserver.interest.entity.UserInterestKeyword;
import com.team.teamreadioserver.interest.enums.InterestStatus;
import com.team.teamreadioserver.interest.repository.UserInterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.UserInterestKeywordRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BookBookmarkRepository bookBookmarkRepository;
    private final EmotionRepository emotionRepository;
    private final UserInterestCategoryRepository userInterestCategoryRepository;
    private final UserInterestKeywordRepository userInterestKeywordRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public ResponseDTO getFeedItems(String mainTab, String subTab, String loginUserId, String userEmotion, String userInterests, Pageable pageable) {

        List<Long> profileIds = Collections.emptyList();
        List<String> bookIsbns = Collections.emptyList();
        Long loginUserProfileId = null;

        Profile loggedInUserProfile = null;
        if (loginUserId != null) {
            loggedInUserProfile = userRepository.findByUserId(loginUserId)
                    .flatMap(user -> profileRepository.findByUser_UserId(user.getUserId()))
                    .orElse(null);
            if (loggedInUserProfile != null) {
                loginUserProfileId = loggedInUserProfile.getProfileId();
            }
        }

        if ("rec".equals(mainTab)) {
            if (loggedInUserProfile == null) {
                System.out.println("비로그인 추천: 시간순");
            } else {
                System.out.println("로그인 추천: 감정/북마크/관심 분야 기반");

                Set<Long> calculatedProfileIds = new HashSet<>();
                Set<String> calculatedIsbns = new HashSet<>();

                Optional<Emotion> latestEmotionOpt = emotionRepository.findTopByUserOrderByCreatedAtDesc(loggedInUserProfile.getUser());
                EmotionType currentUserEmotion = latestEmotionOpt.map(Emotion::getEmotionType).orElse(null);

                if (currentUserEmotion != null) {
                    Set<String> usersWithSameEmotion = emotionRepository.findByEmotionTypeAndDate(currentUserEmotion, LocalDate.now()).stream()
                            .map(emotion -> emotion.getUser().getUserId())
                            .filter(userId -> !userId.equals(loginUserId))
                            .collect(Collectors.toSet());
                    if (!usersWithSameEmotion.isEmpty()) {
                        profileRepository.findByUser_UserIdIn(usersWithSameEmotion).stream()
                                .map(Profile::getProfileId)
                                .forEach(calculatedProfileIds::add);
                    }
                }

                List<BookBookmark> userBookmarks = bookBookmarkRepository.findByUserId(loggedInUserProfile.getUser().getUserId());
                userBookmarks.stream()
                        .map(bookmark -> bookmark.getBook().getBookIsbn())
                        .forEach(calculatedIsbns::add);

                Set<Long> userInterestCategoryIds = userInterestCategoryRepository.findByUserAndStatus(loggedInUserProfile.getUser(), InterestStatus.ACTIVE).stream()
                        .map(UserInterestCategory::getInterestCategory)
                        .map(InterestCategory::getInterestId)
                        .collect(Collectors.toSet());
                if (!userInterestCategoryIds.isEmpty()) {
                    Set<String> usersWithSameCategory = userInterestCategoryRepository.findByInterestCategory_InterestIdInAndStatus(userInterestCategoryIds, InterestStatus.ACTIVE).stream()
                            .map(userInterest -> userInterest.getUser().getUserId())
                            .filter(userId -> !userId.equals(loginUserId))
                            .collect(Collectors.toSet());
                    if (!usersWithSameCategory.isEmpty()) {
                        profileRepository.findByUser_UserIdIn(usersWithSameCategory).stream()
                                .map(Profile::getProfileId)
                                .forEach(calculatedProfileIds::add);
                    }
                }
                Set<Long> userInterestKeywordIds = userInterestKeywordRepository.findByUserAndStatus(loggedInUserProfile.getUser(), InterestStatus.ACTIVE).stream()
                        .map(UserInterestKeyword::getInterestKeyword)
                        .map(InterestKeyword::getInterestKeywordId)
                        .collect(Collectors.toSet());
                if (!userInterestKeywordIds.isEmpty()) {
                    Set<String> usersWithSameKeyword = userInterestKeywordRepository.findByInterestKeyword_InterestKeywordIdInAndStatus(userInterestKeywordIds, InterestStatus.ACTIVE).stream()
                            .map(userInterest -> userInterest.getUser().getUserId())
                            .filter(userId -> !userId.equals(loginUserId))
                            .collect(Collectors.toSet());
                    if (!usersWithSameKeyword.isEmpty()) {
                        profileRepository.findByUser_UserIdIn(usersWithSameKeyword).stream()
                                .map(Profile::getProfileId)
                                .forEach(calculatedProfileIds::add);
                    }
                }

                profileIds = calculatedProfileIds.isEmpty() ? Collections.emptyList() : new ArrayList<>(calculatedProfileIds);
                bookIsbns = calculatedIsbns.isEmpty() ? Collections.emptyList() : new ArrayList<>(calculatedIsbns);
            }

        } else if ("following".equals(mainTab)) {
            System.out.println("팔로잉 피드: 시간순");
            if (loggedInUserProfile == null) {
                return new ResponseDTO(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", null);
            }
            profileIds = followRepository.findByFollower_ProfileId(loggedInUserProfile.getProfileId())
                    .stream()
                    .map(follow -> follow.getFollowing().getProfileId())
                    .collect(Collectors.toList());
            if (profileIds.isEmpty()) {
                System.out.println("팔로우하는 프로필이 없습니다.");
                profileIds = Collections.emptyList();
            }
        }

        Page<FeedItemDto> feedPage = feedRepository.findCombinedFeed(
                subTab,
                profileIds,
                bookIsbns,
                loginUserProfileId,
                pageable
        ).map(projection -> new FeedItemDto(
                projection.getType(),
                projection.getId(),
                projection.getCreatedAt(),
                projection.getProfileId(),
                projection.getUserId(),
                projection.getProfileImg(),
                projection.getUserName(),
                projection.getBookIsbn(),
                projection.getTitle(),
                projection.getContent(),
                projection.getContentImg(),
                projection.getReviewContent(),
                projection.getBookCoverUrl(),
                projection.getBookTitle(),
                projection.getBookAuthor(),
                projection.getLikesCount(),
                projection.getReviewsCount(),
                projection.getIsLiked(),
                projection.getIsFollowing()
        ));

        List<FeedItemDto> finalFeedItems = feedPage.getContent();


        return new ResponseDTO(
                HttpStatus.OK,
                "피드 조회 성공",
                new FeedResponseDto(
                        finalFeedItems,
                        feedPage.getNumber(),
                        feedPage.getTotalElements(),
                        feedPage.getTotalPages(),
                        feedPage.isLast()
                )
        );
    }

    // ✨ 알라딘 API 호출 헬퍼 메서드 제거 (더 이상 사용하지 않으므로)
    // private Map<String, String> fetchBookInfoFromAladin(String isbn) { /* ... */ }

    // 시간 계산 헬퍼 (동일)
    private String calculateTimeAgo(Date date) {
        if (date == null) return "";
        long diff = new Date().getTime() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "일전";
        if (hours > 0) return hours + "시간전";
        if (minutes > 0) return minutes + "분전";
        return seconds + "초전";
    }
}
