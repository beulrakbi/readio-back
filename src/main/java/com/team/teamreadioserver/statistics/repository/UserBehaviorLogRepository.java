package com.team.teamreadioserver.statistics.repository;

import com.team.teamreadioserver.statistics.entity.UserBehaviorLog;
import com.team.teamreadioserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Integer> {

    List<UserBehaviorLog> findByUserAndLogDate(User user, LocalDate logDate);

    List<UserBehaviorLog> findBySectionAndLogDateBetween(String section, LocalDate start, LocalDate end);

    List<UserBehaviorLog> findByLogDateBetween(LocalDate start, LocalDate end);
}