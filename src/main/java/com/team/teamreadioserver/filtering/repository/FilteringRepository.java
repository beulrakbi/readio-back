package com.team.teamreadioserver.filtering.repository;

import com.team.teamreadioserver.filtering.entity.Filtering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FilteringRepository extends JpaRepository<Filtering, Integer> {

    List<Filtering> findByGroupId(int groupId);

}
