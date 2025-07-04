package com.team.teamreadioserver.filtering.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.filtering.dto.FilteringDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDTO;
import com.team.teamreadioserver.filtering.dto.FilteringGroupDetailDTO;
import com.team.teamreadioserver.filtering.entity.Filtering;
import com.team.teamreadioserver.filtering.entity.FilteringGroup;
import com.team.teamreadioserver.filtering.repository.FilteringGroupRepository;
import com.team.teamreadioserver.filtering.repository.FilteringRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(FilteringService.class);
    private final FilteringRepository filteringRepository;
    private final FilteringGroupRepository filteringGroupRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Object insertFilterings(List<FilteringDTO> filteringDTOs, int groupId) {
        log.info("[FilteringService] insertFiltering Start");
        int result = 0;
//        System.out.println("그룹아이디" + groupId);

        try {
            for (FilteringDTO filteringDTO : filteringDTOs) {
                filteringDTO.setGroupId(groupId);
                System.out.println("filteringDTO : " + filteringDTO);
                Filtering filtering = new Filtering(filteringDTO.getFilteringId(), filteringDTO.getGroupId(), filteringDTO.getVideoId(), filteringDTO.getKeyword());
                filteringRepository.save(filtering);
                result = 1;
            }
        } catch (Exception e) {
            log.error("[FilteringService] insertFiltering Fail");
            throw e;
        }

        log.info("[FilteringService] insertFiltering End");

        return (result > 0) ? "필터링 입력 성공" : "필터링 입력 실패";
    }

    @Transactional
    public int insertFilteringGroup(FilteringGroupDTO filteringGroupDTO) {
        log.info("[FilteringService] insertFilteringGroup Start");
        int result = 0;
        FilteringGroup filteringGroup;
        try {
            filteringGroup = new FilteringGroup(filteringGroupDTO.getTitle(), filteringGroupDTO.getContent(), filteringGroupDTO.getTypeId());
            filteringGroupRepository.save(filteringGroup);
            result = 1;
        } catch (Exception e) {
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
        } catch (Exception e) {
            log.error("[FilteringService] updateFilteringGroupActiveState() Fail");
        }
        log.info("[FilteringService] updateFilteringGroupActiveState() End");

        return (result > 0) ? "필터링 그룹 상태 수정 성공" : "필터링 그룹 상태 수정 실패";
    }

    @Transactional
    public Object updateFilteringGroup(FilteringGroupDetailDTO filteringGroupDetailDTO)
    {
        List<FilteringDTO> filteringDTOS = filteringGroupDetailDTO.getFilterings();
        FilteringGroupDTO filteringGroupDTO = filteringGroupDetailDTO.getFilteringGroup();
        int result = 0;

        try {
            FilteringGroup foundFilteringGroup = filteringGroupRepository.findByGroupId(filteringGroupDTO.getGroupId());
            List<Filtering> foundFilters = filteringRepository.findByGroupId(filteringGroupDTO.getGroupId());
            List<Filtering> newFilters = filteringDTOS.stream().map(filter -> modelMapper.map(filter, Filtering.class)).collect(Collectors.toList());
            foundFilteringGroup.modifyFilteringGroup(filteringGroupDTO.getTitle(), filteringGroupDTO.getContent(), filteringGroupDTO.getTypeId());

            for (Filtering oldFilter : foundFilters) {
                if (!newFilters.contains(oldFilter))
                {
                    filteringRepository.delete(oldFilter);
                }
                else
                {
                    newFilters.remove(oldFilter);
                }
            }

            filteringRepository.saveAll(newFilters);
            result = 1;

        } catch (Exception e)
        {
            log.error("[FilteringService] updateFilteringGroup() Fail");
        }

        return (result > 0) ? "필터링 그룹 상태 수정 성공" : "필터링 그룹 상태 수정 실패" ;
    }


    public int selectFilteringGroups() {
        log.info("[FilteringService] selectFilteringGroup() Start");

        int result = filteringGroupRepository.findAllBy().size();
        System.out.println("result" + result);
        log.info("[FilteringService] selectFilteringGroup() End");

        return result;
    }

    public Object selectFilteringGroupWithPaging(Criteria cri) {
        log.info("[FilteringService] selectFilteringGroupWithPaging Start");

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("groupId").descending());

        Page<FilteringGroup> result = filteringGroupRepository.findAllBy(paging);
        List<FilteringGroup> filteringGroups = result.getContent();

        log.info("[FilteringService] selectFilteringGroup End");
        return filteringGroups.stream().map(filteringGroup -> modelMapper.map(filteringGroup, FilteringGroupDTO.class)).collect(Collectors.toList());
    }

    public FilteringGroupDTO selectFilteringGroup(int groupId) {
        return modelMapper.map(filteringGroupRepository.findByGroupId(groupId), FilteringGroupDTO.class);
    }

    public List<FilteringDTO> selectFilterings(int groupId) {
        return filteringRepository.findByGroupId(groupId).stream().map(filtering -> modelMapper.map(filtering, FilteringDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public Object removeFilteringGroup(int groupId) {
        int result = 0;
        try {
            filteringRepository.deleteAll(filteringRepository.findByGroupId(groupId));
            filteringGroupRepository.delete(filteringGroupRepository.findByGroupId(groupId));
            result = 1;
        } catch (Exception e) {
            log.error("[FilteringService] removeFilteringGroup() Fail");
        }
        return (result > 0) ? "필터링 그룹 삭제 성공" : "필터링 그룹 삭제 실패";
    }

    public List<FilteringDTO> getFilters(int typeId) {

        List<FilteringGroup> groups = filteringGroupRepository.findByTypeIdAndIsActive(typeId,"Y");
        List<Filtering> filterings = new ArrayList<>();
        for (FilteringGroup filteringGroup : groups) {
            filterings.addAll(filteringRepository.findByGroupId(filteringGroup.getGroupId()));
        }
        List<FilteringDTO> result = new ArrayList<>();
        for (Filtering filtering : filterings) {
            FilteringDTO dto = modelMapper.map(filtering, FilteringDTO.class);
            dto.setFilteringId(filtering.getFilteringId());
            dto.setGroupId(filtering.getGroupId());
            dto.setKeyword(filtering.getKeyword());
            dto.setVideoId(filtering.getVideoId());

            result.add(dto);
        }

        return result;
    }

}
