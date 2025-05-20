package com.team.teamreadioserver.interest.service;

import com.team.teamreadioserver.interest.dto.InterestAdminRequestDTO;
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

}
