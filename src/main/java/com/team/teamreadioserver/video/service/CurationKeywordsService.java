package com.team.teamreadioserver.video.service;

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

    public CurationTypeDTO selectCurationType(int TypeId) {
        return modelMapper.map(curationTypeRepository.findByTypeId(TypeId), CurationTypeDTO.class);
    }

    public List<CurationTypeDTO> selectAllCurationTypes() {

        List<CurationTypeDTO> result = curationTypeRepository.findAll().stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());
        Collections.shuffle(result);
        return result;
    }

    public List<CurationDTO> selectAllCurationTypesAndKeywords() {

        List<CurationDTO> result = new ArrayList<>();
        List<CurationTypeDTO> curationTypeDTOS = curationTypeRepository.findAll().stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());

        for (CurationTypeDTO curationTypeDTO : curationTypeDTOS) {
            CurationDTO curationDTO = new CurationDTO(curationTypeDTO, curationKeywordsRepository.findByTypeIdOrderByTypeId(curationTypeDTO.getTypeId()).stream()
                    .map(keyword -> modelMapper.map(keyword, CurationKeywordsDTO.class)).collect(Collectors.toList()));
            result.add(curationDTO);
        }
        return result;
    }

    public List<CurationKeywordsDTO> selectCurationKeywordsByTypeId(int typeId)
    {
        List<CurationKeywords> curationKeywords = curationKeywordsRepository.findByTypeIdOrderByTypeId(typeId);
        Collections.shuffle(curationKeywords);
        int toIndex = Math.min(curationKeywords.size(), 5);
        curationKeywords = curationKeywords.subList(0, toIndex);
        List<CurationKeywordsDTO> result = new ArrayList<>();
        for (CurationKeywords curationKeyword : curationKeywords)
        {
            CurationKeywordsDTO dto = modelMapper.map(curationKeyword, CurationKeywordsDTO.class);
            result.add(dto);
        }

        return result;
    }
    
    @Transactional
    public Object updateAll(CurationDTO curationDTO)
    {  
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
    public Object updateCurationType(CurationTypeDTO curationTypeDTO)
    {
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
    public Object insertCurationKeywords(List<CurationKeywordsDTO> curationKeywordsDTOS)
    {
        int result = 0;
        try {
            List<CurationKeywords> foundKeywords = curationKeywordsRepository.findByTypeIdOrderByTypeId(curationKeywordsDTOS.get(0).getTypeId());
            List<CurationKeywords> newCurationKeywords = curationKeywordsDTOS.stream().map(newKey -> modelMapper.map(newKey, CurationKeywords.class)).collect(Collectors.toList());

            for (CurationKeywords old : foundKeywords)
            {
                if (!newCurationKeywords.contains(old))
                {
                    curationKeywordsRepository.delete(old);
                }
                else
                {
                    newCurationKeywords.remove(old);
                }
            }

            curationKeywordsRepository.saveAll(newCurationKeywords);
            result = 1;

        } catch (Exception e)
        {
            log.error("[CurationService] insertCurationKeywords() Fail");
        }

        return (result > 0) ? "큐레이션 키워드 목록 수정 성공" : "큐레이션 키워드 목록 수정 실패";
    }

}
