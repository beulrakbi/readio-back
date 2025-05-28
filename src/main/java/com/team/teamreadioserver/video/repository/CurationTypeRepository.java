package com.team.teamreadioserver.video.repository;

import com.team.teamreadioserver.video.entity.CurationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationTypeRepository extends JpaRepository<CurationType, Integer> {

    CurationType findByTypeId(int typeId);
}
