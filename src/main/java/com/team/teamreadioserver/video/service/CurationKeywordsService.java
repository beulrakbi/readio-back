package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.bookmark.entity.BookBookmark;
import com.team.teamreadioserver.bookmark.repository.BookBookmarkRepository;
import com.team.teamreadioserver.interest.entity.UserInterestCategory;
import com.team.teamreadioserver.interest.entity.UserInterestKeyword;
import com.team.teamreadioserver.interest.repository.InterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.UserInterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.UserInterestKeywordRepository;
import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.CurationTypeDTO;
import com.team.teamreadioserver.video.entity.CurationKeywords;
import com.team.teamreadioserver.video.entity.CurationType;
import com.team.teamreadioserver.video.repository.CurationKeywordsRepository;
import com.team.teamreadioserver.video.repository.CurationTypeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurationKeywordsService {

    private final CurationKeywordsRepository curationKeywordsRepository;
    private final CurationTypeRepository curationTypeRepository;
    private static final Logger log = LoggerFactory.getLogger(CurationKeywordsService.class);
    private final ModelMapper modelMapper;
    private final UserInterestCategoryRepository userInterestCategoryRepository;
    private final InterestCategoryRepository interestCategoryRepository;
    private final BookBookmarkRepository bookBookmarkRepository;
    private final UserInterestKeywordRepository userInterestKeywordRepository;

    public CurationTypeDTO selectCurationType(int typeId) {

        return modelMapper.map(curationTypeRepository.findByTypeId(typeId), CurationTypeDTO.class);
    }

    public List<CurationTypeDTO> selectBasicCurationTypes() {

        List<CurationTypeDTO> result = curationTypeRepository.findAllByTypeIdLessThanEqual(5).stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());
        Collections.shuffle(result);
        return result;
    }

    public List<CurationTypeDTO> selectAllCurationTypes() {
        List<CurationTypeDTO> result = curationTypeRepository.findAllByTypeIdLessThanEqual(100).stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());
        Collections.shuffle(result);
        return result;
    }

    public List<CurationTypeDTO> selectAllCurationTypesOrderByTypeId() {
        List<CurationTypeDTO> result = curationTypeRepository.findAllByTypeIdLessThanEqualOrderByTypeId(100).stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());
        return result;
    }


    public List<CurationDTO> selectAllCurationTypesAndKeywords() {

        List<CurationDTO> result = new ArrayList<>();
        List<CurationTypeDTO> curationTypeDTOS = curationTypeRepository.findAll().stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());

        for (CurationTypeDTO curationTypeDTO : curationTypeDTOS) {
            CurationDTO curationDTO = new CurationDTO(curationTypeDTO, curationKeywordsRepository.findByTypeIdOrderByTypeId(curationTypeDTO.getTypeId()).stream().map(keyword -> modelMapper.map(keyword, CurationKeywordsDTO.class)).collect(Collectors.toList()));
            result.add(curationDTO);
        }
        return result;
    }

    public List<CurationKeywordsDTO> selectCurationKeywordsByTypeId(int typeId, String userId) {
        List<CurationKeywordsDTO> result = new ArrayList<>();
        if (typeId < 6) {

            List<CurationKeywords> curationKeywords = curationKeywordsRepository.findByTypeIdOrderByTypeId(typeId);
            Collections.shuffle(curationKeywords);
            int toIndex = Math.min(curationKeywords.size(), 5);
            curationKeywords = curationKeywords.subList(0, toIndex);
            for (CurationKeywords curationKeyword : curationKeywords) {
                CurationKeywordsDTO dto = modelMapper.map(curationKeyword, CurationKeywordsDTO.class);
                result.add(dto);
            }

            return result;
        }
        else if (typeId == 7)
        {
            List<UserInterestCategory> userInterestCategories = userInterestCategoryRepository.findByUser_UserId(userId);
            Collections.shuffle(userInterestCategories);
            int toIndex = Math.min(userInterestCategories.size(), 5);
            userInterestCategories = userInterestCategories.subList(0, toIndex);

            for (UserInterestCategory userInterestCategory : userInterestCategories) {
                CurationKeywordsDTO dto = new CurationKeywordsDTO();
                dto.setCurationId(0);
                dto.setKeyword(userInterestCategory.getInterestCategory().getInterestCategory());
                dto.setTypeId(7);
                result.add(dto);
            }

            return result;
        }
        else if (typeId == 8)
        {
            List<BookBookmark> bookBookmarks = bookBookmarkRepository.findByUserId(userId);
            Collections.shuffle(bookBookmarks);
            int toIndex = Math.min(bookBookmarks.size(), 5);
            bookBookmarks = bookBookmarks.subList(0, toIndex);
            for (BookBookmark bookBookmark : bookBookmarks) {
                CurationKeywordsDTO dto = new CurationKeywordsDTO();
                dto.setCurationId(0);
                String bookName = bookBookmark.getBook().getBookTitle();
                if (bookName.contains(" - ")){
                    bookName = bookName.substring(0,bookName.indexOf(" - "));
                }
                dto.setKeyword(bookName);
                dto.setTypeId(8);

                result.add(dto);
            }
            return result;
        }
        else if (typeId == 9)
        {
            List<UserInterestKeyword> userInterestKeywords = userInterestKeywordRepository.findByUser_UserId(userId);
            Collections.shuffle(userInterestKeywords);
            int toIndex = Math.min(userInterestKeywords.size(), 5);
            userInterestKeywords = userInterestKeywords.subList(0, toIndex);

            for (UserInterestKeyword userInterestKeyword : userInterestKeywords) {
                CurationKeywordsDTO dto = new CurationKeywordsDTO();
                dto.setCurationId(0);
                dto.setKeyword(userInterestKeyword.getInterestKeyword().getInterestKeyword());
                dto.setTypeId(9);
                result.add(dto);
            }
            return result;
        }

        return null;
    }

    @Transactional
    public Object updateAll(CurationDTO curationDTO) {
        int result = 0;
        try {
            updateCurationType(curationDTO.getCurationType());
            insertCurationKeywords(curationDTO.getCurationKeywords());
            result = 1;
        } catch (Exception e) {
            log.error("[CurationService] updateAll() Fail");
        }
        return (result > 0) ? "큐레이션 전체 수정 성공" : "큐레이션 전체 수정 실패";
    }

    @Transactional
    public Object updateCurationType(CurationTypeDTO curationTypeDTO) {
        int result = 0;
        try {
            CurationType curationType = curationTypeRepository.findByTypeId(curationTypeDTO.getTypeId());
            curationType.modifyTypeText(curationTypeDTO.getTypeText());
            result = 1;
        } catch (Exception e) {
            log.error("[CurationService] updateCurationType() Fail");
        }
        return (result > 0) ? "큐레이션 타입 수정 성공" : "큐레이션 타입 수정 실패";
    }

    @Transactional
    public Object insertCurationKeywords(List<CurationKeywordsDTO> curationKeywordsDTOS) {
        int result = 0;
        try {
            List<CurationKeywords> foundKeywords = curationKeywordsRepository.findByTypeIdOrderByTypeId(curationKeywordsDTOS.get(0).getTypeId());
            List<CurationKeywords> newCurationKeywords = curationKeywordsDTOS.stream().map(newKey -> modelMapper.map(newKey, CurationKeywords.class)).collect(Collectors.toList());

            for (CurationKeywords old : foundKeywords) {
                if (!newCurationKeywords.contains(old)) {
                    curationKeywordsRepository.delete(old);
                } else {
                    newCurationKeywords.remove(old);
                }
            }

            curationKeywordsRepository.saveAll(newCurationKeywords);
            result = 1;

        } catch (Exception e) {
            log.error("[CurationService] insertCurationKeywords() Fail");
        }

        return (result > 0) ? "큐레이션 키워드 목록 수정 성공" : "큐레이션 키워드 목록 수정 실패";
    }

}
