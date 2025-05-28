package com.team.teamreadioserver.interest.service;

import com.team.teamreadioserver.interest.dto.admin.InterestDTO;
import com.team.teamreadioserver.interest.dto.user.InterestUserRequestDTO;
import com.team.teamreadioserver.interest.dto.user.InterestUserResponseDTO;
import com.team.teamreadioserver.interest.entity.InterestCategory;
import com.team.teamreadioserver.interest.entity.InterestKeyword;
import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import com.team.teamreadioserver.interest.entity.UserInterestKeyword;
import com.team.teamreadioserver.interest.enums.InterestStatus;
import com.team.teamreadioserver.interest.repository.InterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.InterestKeywordRepository;
import com.team.teamreadioserver.interest.repository.UserInterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.UserInterestKeywordRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestUserService {

    private final UserRepository userRepository;
    private final InterestCategoryRepository interestCategoryRepository;
    private final InterestKeywordRepository interestKeywordRepository;
    private final UserInterestCategoryRepository userInterestCategoryRepository;
    private final UserInterestKeywordRepository userInterestKeywordRepository;
    private final InterestCategoryRepository categoryRepository;
    private final InterestKeywordRepository keywordRepository;

    @Transactional
    public void registerInterests(InterestUserRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        for (Long categoryId : dto.getCategoryIds()) {
            if (!userInterestCategoryRepository.existsByUser_UserIdAndInterestCategory_InterestId(user.getUserId(), categoryId)) {
                InterestCategory category = interestCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
                userInterestCategoryRepository.save(UserInterestCategory.builder()
                        .user(user)
                        .interestCategory(category)
                        .status(InterestStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        for (Long keywordId : dto.getKeywordIds()) {
            if (!userInterestKeywordRepository.existsByUser_UserIdAndInterestKeyword_InterestKeywordId(user.getUserId(), keywordId)) {
                InterestKeyword keyword = interestKeywordRepository.findById(keywordId)
                        .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
                userInterestKeywordRepository.save(UserInterestKeyword.builder()
                        .user(user)
                        .interestKeyword(keyword)
                        .status(InterestStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }
    }

    //  조회 메서드는 별도 정의
    @Transactional(readOnly = true)
    public InterestUserResponseDTO getInterestsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<UserInterestCategory> categoryList = userInterestCategoryRepository.findByUser_UserId(userId);
        System.out.println("조회된 카테고리 수: " + categoryList.size());
        List<UserInterestKeyword> keywordList = userInterestKeywordRepository.findByUser_UserId(userId);
        System.out.println("조회된 키워드 수: " + keywordList.size());

        List<InterestUserResponseDTO.SimpleInterestDTO> categories = categoryList.stream()
                .filter(cat -> {
                    System.out.println("카테고리 상태: " + cat.getStatus()); // ✅ 로그 확인
                    return cat.getStatus() == InterestStatus.ACTIVE;
                })
                .map(cat -> {
                    System.out.println("카테고리 이름: " + cat.getInterestCategory().getInterestCategory()); // ✅ name 확인
                    return new InterestUserResponseDTO.SimpleInterestDTO(
                            cat.getInterestCategory().getInterestId(),
                            cat.getInterestCategory().getInterestCategory()
                    );
                })
                .toList();
        List<InterestUserResponseDTO.SimpleInterestDTO> keywords = keywordList.stream()
                .filter(kw -> kw.getStatus() == InterestStatus.ACTIVE)
                .map(kw -> new InterestUserResponseDTO.SimpleInterestDTO(
                        kw.getInterestKeyword().getInterestKeywordId(),
                        kw.getInterestKeyword().getInterestKeyword()))
                .toList();

        return InterestUserResponseDTO.builder()
                .userId(userId)
                .categories(categories)
                .keywords(keywords)
                .build();
    }

    @Transactional
    public void updateInterests(InterestUserRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 기존 관심 카테고리 조회
        List<UserInterestCategory> existingCategories = userInterestCategoryRepository.findByUser_UserId(user.getUserId());

        // 2. 요청한 ID Set
        Set<Long> newCategoryIds = dto.getCategoryIds() == null ? new HashSet<>() : new HashSet<>(dto.getCategoryIds());
        // 3. 기존 항목 중 체크 해제된 것 → DELETED 처리
        for (UserInterestCategory existing : existingCategories) {
            Long categoryId = existing.getInterestCategory().getInterestId();
            if (!newCategoryIds.contains(categoryId)) {
                existing.setStatus(InterestStatus.DELETED);
            } else {
                // 이 항목은 다시 선택된 항목이므로 되살림 필요
                existing.setStatus(InterestStatus.ACTIVE);
                newCategoryIds.remove(categoryId);
            }
        }

        // 4. 새로 추가된 항목 → INSERT
        for (Long newId : newCategoryIds) {
            InterestCategory category = interestCategoryRepository.findById(newId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
            UserInterestCategory newUserCat = UserInterestCategory.builder()
                    .user(user)
                    .interestCategory(category)
                    .status(InterestStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
            userInterestCategoryRepository.save(newUserCat);
        }

        // 키워드도 같은 방식으로 반복
        List<UserInterestKeyword> existingKeywords = userInterestKeywordRepository.findByUser_UserId(user.getUserId());
        Set<Long> newKeywordIds = dto.getKeywordIds() == null ? new HashSet<>() : new HashSet<>(dto.getKeywordIds());
        for (UserInterestKeyword existing : existingKeywords) {
            Long keywordId = existing.getInterestKeyword().getInterestKeywordId();
            if (!newKeywordIds.contains(keywordId)) {
                existing.setStatus(InterestStatus.DELETED);
            } else {
                existing.setStatus(InterestStatus.ACTIVE);
                newKeywordIds.remove(keywordId);
            }
        }

        for (Long newId : newKeywordIds) {
            InterestKeyword keyword = interestKeywordRepository.findById(newId)
                    .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
            UserInterestKeyword newUserKeyword = UserInterestKeyword.builder()
                    .user(user)
                    .interestKeyword(keyword)
                    .status(InterestStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
            userInterestKeywordRepository.save(newUserKeyword);
        }
    }

    public List<InterestDTO> getAllCategoriesWithId() {
        return categoryRepository.findAll().stream()
                .map(c -> new InterestDTO(c.getInterestId(), c.getInterestCategory()))
                .toList();
    }

    public List<InterestDTO> getAllKeywordsWithId() {
        return keywordRepository.findAll().stream()
                .map(k -> new InterestDTO(k.getInterestKeywordId(), k.getInterestKeyword()))
                .toList();
    }

}
