package com.team.teamreadioserver.search.repository;

import com.team.teamreadioserver.search.entity.CurationKeywords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurationRepository extends JpaRepository<CurationKeywords, Long > {

    // type 컬럼으로 CurationKeyword 엔티티 목록을 찾는 메소드
    List<CurationKeywords> findByType(String type);
}
