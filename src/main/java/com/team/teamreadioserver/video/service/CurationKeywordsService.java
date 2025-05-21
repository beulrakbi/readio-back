package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.video.dto.CurationKeywordsDTO;
import com.team.teamreadioserver.video.entity.CurationKeywords;
import com.team.teamreadioserver.video.repository.CurationKeywordsRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class CurationKeywordsService {

    private final CurationKeywordsRepository curationKeywordsRepository;
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    private final ModelMapper modelMapper;

    public List<CurationKeywordsDTO> selectCurationKeywordsByType(String type)
    {
        List<CurationKeywords> curationKeywords = curationKeywordsRepository.findByType(type);
        System.out.println("타입: " + type);
        System.out.println("어쩌구" + curationKeywords);
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
