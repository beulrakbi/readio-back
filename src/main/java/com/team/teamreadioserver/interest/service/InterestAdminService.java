package com.team.teamreadioserver.interest.service;

import com.team.teamreadioserver.interest.dto.admin.InterestDTO;
import com.team.teamreadioserver.interest.dto.admin.InterestSaveResultDTO;
import com.team.teamreadioserver.interest.entity.InterestCategory;
import com.team.teamreadioserver.interest.entity.InterestKeyword;
import com.team.teamreadioserver.interest.repository.InterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.InterestKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestAdminService {

    private final InterestCategoryRepository categoryRepository;
    private final InterestKeywordRepository keywordRepository;

    @Transactional
    //등록
    public InterestSaveResultDTO registerAll(List<String> categories, List<String> keywords) {
        List<String> savedCategories = new ArrayList<>();
        List<String> savedKeywords = new ArrayList<>();

        for (String category : categories) {
            if (!categoryRepository.existsByInterestCategory(category)) {
                categoryRepository.save(new InterestCategory(null, category, LocalDateTime.now()));
                savedCategories.add(category);
            }
        }

        for (String keyword : keywords) {
            if (!keywordRepository.existsByInterestKeyword(keyword)) {
                keywordRepository.save(new InterestKeyword(null, keyword, LocalDateTime.now()));
                savedKeywords.add(keyword);
            }
        }

        return new InterestSaveResultDTO(savedCategories, savedKeywords);
    }
    //조회
    public List<String> getAllCategories() {
        System.out.println("[getAllCategories] 호출됨");
        List<InterestCategory> list = categoryRepository.findAll();
        System.out.println("[getAllCategories] 조회 결과:" + list.size());

        for (InterestCategory category : list) {
            System.out.printf("[getAllCategories] %s\n", category);
        }
        return categoryRepository.findAll().stream()
                .map(InterestCategory::getInterestCategory)
                .toList();
    }

    public List<String> getAllKeywords() {
        System.out.println("[getAllKeywords] 호출됨");
        List<InterestKeyword> list = keywordRepository.findAll();
        System.out.println("[getAllKeywords] 조회 결과: " + list.size());

        for (InterestKeyword keyword : list) {
            System.out.printf("[getAllKeywords] %s\n", keyword);
        }

        return list.stream()
                .map(InterestKeyword::getInterestKeyword)
                .toList();
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
    //수정
    @Transactional
    public void updateCategory(Long interestId, String newName) {
        InterestCategory category = categoryRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        category.setInterestCategory(newName);
        category.setCreatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateKeyword(Long interestKeywordId, String newName) {
        InterestKeyword keyword = keywordRepository.findById(interestKeywordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 키워드를 찾을 수 없습니다."));
        keyword.setInterestKeyword(newName);
        keyword.setCreatedAt(LocalDateTime.now());

    }

    //삭제
    @Transactional
    public void deleteCategory(Long  interestId) {
        InterestCategory category = categoryRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        categoryRepository.delete(category);
    }

    @Transactional
    public void deleteKeyword(Long interestKeywordId) {
        InterestKeyword keyword = keywordRepository.findById(interestKeywordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 키워드를 찾을 수 없습니다."));
        keywordRepository.delete(keyword);
    }


}
