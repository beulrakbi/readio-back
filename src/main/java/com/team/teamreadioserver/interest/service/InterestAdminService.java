package com.team.teamreadioserver.interest.service;

import com.team.teamreadioserver.interest.dto.InterestSaveResultDTO;
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
        return categoryRepository.findAll().stream()
                .map(InterestCategory::getInterestCategory)
                .toList();
    }

    public List<String> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(InterestKeyword::getInterestKeyword)
                .toList();
    }

    //수정
    @Transactional
    public void updateCategory(Long interestId, String newName) {
        InterestCategory category = categoryRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        category.setInterestCategory(newName);
    }

    @Transactional
    public void updateKeyword(Long interestKeywordId, String newName) {
        InterestKeyword keyword = keywordRepository.findById(interestKeywordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 키워드를 찾을 수 없습니다."));
        keyword.setInterestKeyword(newName);
    }

}
