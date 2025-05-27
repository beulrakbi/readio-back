package com.team.teamreadioserver.filtering.service;

import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.entity.Filtering;
import com.team.teamreadioserver.filtering.entity.FilteringGroup;
import com.team.teamreadioserver.filtering.repository.FilteringGroupRepository;
import com.team.teamreadioserver.filtering.repository.FilteringRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(FilteringService.class);
    private final FilteringRepository filteringRepository;
    private final FilteringGroupRepository filteringGroupRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertFiltering(FilteringDTO filteringDTO, int groupId)
    {
        log.info("[FilteringService] insertFiltering Start");
        int result = 0;

        try {
            filteringDTO.setGroupId(groupId);
            Filtering filtering = modelMapper.map(filteringDTO, Filtering.class);
            filteringRepository.save(filtering);
            result = 1;
        }
        catch (Exception e)
        {
            log.error("[FilteringService] insertFiltering Fail");
        }

        log.info("[FilteringService] insertFiltering End");

        return (result > 0) ? "필터링 입력 성공" : "필터링 입력 실패" ;
    }

    @Transactional
    public Object insertFilteringGroup(FilteringGroupDTO filteringGroupDTO)
    {
        log.info("[FilteringService] insertFilteringGroup Start");
        int result = 0;

        try {
            FilteringGroup filteringGroup = modelMapper.map(filteringGroupDTO, FilteringGroup.class);
            filteringGroupRepository.save(filteringGroup);
            result = 1;
        }
        catch (Exception e)
        {
            log.error("[FilteringService] insertFilteringGroup Fail");
        }

        log.info("[FilteringService] insertFilteringGroup End");

        return (result > 0) ? "필터링 그룹 생성 성공" : "필터링 그룹 생성 실패" ;
    }

}
