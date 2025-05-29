package com.team.teamreadioserver.clicklog.repository;

import com.team.teamreadioserver.clicklog.entity.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

}