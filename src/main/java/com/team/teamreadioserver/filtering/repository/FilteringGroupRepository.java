package com.team.teamreadioserver.filtering.repository;

import com.team.teamreadioserver.filtering.entity.FilteringGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilteringGroupRepository extends JpaRepository<FilteringGroup, Integer> {

    List<FilteringGroup> findAllBy();

    Page<FilteringGroup> findAllBy(Pageable paging);

    FilteringGroup findByGroupId(int groupId);
}
