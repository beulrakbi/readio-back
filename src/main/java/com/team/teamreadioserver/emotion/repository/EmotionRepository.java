package com.team.teamreadioserver.emotion.repository;

import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    // 특정 날짜에 등록된 감정 1건만
    Optional<Emotion> findFirstByUser_UserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);

    // 해당 월에 등록된 감정 전체 조회
    List<Emotion> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // 해당 날짜에 등록된 감정이 있는지 조회
    Optional<Emotion> findByUserAndDate(User user, LocalDate date);

}
