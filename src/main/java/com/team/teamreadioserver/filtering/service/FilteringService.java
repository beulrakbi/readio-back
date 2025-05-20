package com.team.teamreadioserver.filtering.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.entity.Filtering;
import com.team.teamreadioserver.filtering.entity.FilteringGroup;
import com.team.teamreadioserver.filtering.repository.FilteringGroupRepository;
import com.team.teamreadioserver.filtering.repository.FilteringRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(FilteringService.class);
    private final FilteringRepository filteringRepository;
    private final FilteringGroupRepository filteringGroupRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertFilterings(List<FilteringDTO> filteringDTOs, int groupId)
    {
        log.info("[FilteringService] insertFiltering Start");
        int result = 0;
//        System.out.println("그룹아이디" + groupId);

        try {
            for(FilteringDTO filteringDTO : filteringDTOs)
            {
                filteringDTO.setGroupId(groupId);
                System.out.println("filteringDTO : " + filteringDTO);
                Filtering filtering = new Filtering(filteringDTO.getFilteringId(), filteringDTO.getGroupId(), filteringDTO.getVideoId(), filteringDTO.getKeyword());
                filteringRepository.save(filtering);
                result = 1;
            }
        }
        catch (Exception e)
        {
            log.error("[FilteringService] insertFiltering Fail");
            throw e;
        }

        log.info("[FilteringService] insertFiltering End");

        return (result > 0) ? "필터링 입력 성공" : "필터링 입력 실패" ;
    }

    @Transactional
    public int insertFilteringGroup(FilteringGroupDTO filteringGroupDTO)
    {
        log.info("[FilteringService] insertFilteringGroup Start");
        int result = 0;
        FilteringGroup filteringGroup;
        try {
            filteringGroup = new FilteringGroup(filteringGroupDTO.getTitle(), filteringGroupDTO.getContent());
            filteringGroupRepository.save(filteringGroup);
            result = 1;
        }
        catch (Exception e)
        {
            log.error("[FilteringService] insertFilteringGroup Fail", e);
            throw e;
        }

        log.info("[FilteringService] insertFilteringGroup End");

        return filteringGroup.getGroupId();
    }

    @Transactional
    public Object updateFilteringGroupActiveState(FilteringGroupDTO filteringGroupDTO) {
        log.info("[FilteringService] updateFilteringGroupActiveState() Start");
        int result = 0;
        try {
            FilteringGroup foundFilteringGroup = filteringGroupRepository.findByGroupId(filteringGroupDTO.getGroupId());
            foundFilteringGroup.modifyFilteringGroupActiveState();
            result = 1;
        } catch (Exception e)
        {
            log.error("[FilteringService] updateFilteringGroupActiveState() Fail");
        }
        log.info("[FilteringService] updateFilteringGroupActiveState() End");

        return (result > 0) ? "필터링 그룹 상태 수정 성공" : "필터링 그룹 상태 수정 실패" ;
    }

    public int selectFilteringGroups()
    {
        log.info("[FilteringService] selectFilteringGroup() Start");

        int result = filteringGroupRepository.findAllBy().size();
        System.out.println("result" + result);
        log.info("[FilteringService] selectFilteringGroup() End");

        return result;
    }

    public Object selectFilteringGroupWithPaging(Criteria cri)
    {
        log.info("[FilteringService] selectFilteringGroupWithPaging Start");

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("groupId").descending());

        Page<FilteringGroup> result = filteringGroupRepository.findAllBy(paging);
        List<FilteringGroup> filteringGroups = (List<FilteringGroup>)result.getContent();

        log.info("[FilteringService] selectFilteringGroup End");
        return filteringGroups.stream().map(filteringGroup -> modelMapper.map(filteringGroup, FilteringGroupDTO.class)).collect(Collectors.toList());
    }

    public FilteringGroupDTO selectFilteringGroup(int groupId)
    {
        return modelMapper.map(filteringGroupRepository.findByGroupId(groupId), FilteringGroupDTO.class);
    }

    public List<FilteringDTO> selectFilterings(int groupId)
    {
        return filteringRepository.findByGroupId(groupId).stream().map(filtering -> modelMapper.map(filtering, FilteringDTO.class)).collect(Collectors.toList());
    }


}
