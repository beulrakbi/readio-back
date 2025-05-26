package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.video.dto.CurationDTO;
import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.dto.CurationTypeDTO;
import com.team.teamreadioserver.video.entity.CurationKeywords;
import com.team.teamreadioserver.video.repository.CurationKeywordsRepository;
import com.team.teamreadioserver.video.repository.CurationTypeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        System.out.printf("testtestsetse: " + result);
        Collections.shuffle(result);
        return result;
    }

    public List<CurationDTO> selectAllCurationTypesAndKeywords() {

        List<CurationDTO> result = new ArrayList<>();
        List<CurationTypeDTO> curationTypeDTOS = curationTypeRepository.findAll().stream().map(type -> modelMapper.map(type, CurationTypeDTO.class)).collect(Collectors.toList());

        for (CurationTypeDTO curationTypeDTO : curationTypeDTOS) {
            CurationDTO curationDTO = new CurationDTO(curationTypeDTO, curationKeywordsRepository.findByTypeId(curationTypeDTO.getTypeId()).stream()
                    .map(keyword -> modelMapper.map(keyword, CurationKeywordsDTO.class)).collect(Collectors.toList()));
            result.add(curationDTO);
        }
        return result;
    }

    public List<CurationKeywordsDTO> selectCurationKeywordsByTypeId(int typeId)
    {
        List<CurationKeywords> curationKeywords = curationKeywordsRepository.findByTypeId(typeId);
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

}
