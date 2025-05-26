package com.team.teamreadioserver.video.repository;

import com.team.teamreadioserver.video.entity.CurationKeywords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurationKeywordsRepository extends JpaRepository<CurationKeywords, Integer> {

    List<CurationKeywords> findByTypeId(int typeId);

}
