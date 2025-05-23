package com.team.teamreadioserver.emotion.service;

import com.team.teamreadioserver.emotion.dto.EmotionRequestDTO;
import com.team.teamreadioserver.emotion.dto.EmotionResponseDTO;
import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.emotion.repository.EmotionRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;

    @Transactional
    public void saveEmotion(EmotionRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate date = dto.getDate();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Emotion existing = emotionRepository
                .findFirstByUser_UserIdAndCreatedAtBetween(user.getUserId(), startOfDay, endOfDay)
                .orElse(null);

        if (existing != null) {
            existing.setEmotionType(dto.getEmotionType());
        } else {
            Emotion newEmotion = Emotion.builder()
                    .user(user)
                    .emotionType(dto.getEmotionType())
                    .createdAt(startOfDay) // 감정 등록일 기준
                    .build();
            emotionRepository.save(newEmotion);
        }
    }

    //조회
    public List<EmotionResponseDTO> getEmotionsByMonth(String userId, int year, int month) {
        // 1. 유저 조회 및 예외 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 월 시작/끝 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 3. 감정 조회
        List<Emotion> emotions = emotionRepository
                .findByUser_UserIdAndCreatedAtBetween(user.getUserId(), startDateTime, endDateTime);

        // 4. 응답 DTO로 변환
        return emotions.stream()
                .map(e -> new EmotionResponseDTO(
                        e.getEmotionType().name(),      // "HAPPY"
                        e.getEmotionType().getLabel(),  // "기쁨"
                        e.getCreatedAt().toLocalDate()  // 2025-06-01
                ))
                .toList();
    }

    @Transactional
    public void updateEmotion(EmotionRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        Optional<Emotion> existing = emotionRepository.findByUserAndDate(user, requestDTO.getDate());


        Emotion emotion = existing.orElseGet(() -> Emotion.builder()
                .user(user)
                .emotionType(requestDTO.getEmotionType())
                .date(requestDTO.getDate())
                .createdAt(LocalDateTime.now())
                .build()
        );

        if (existing.isPresent()){
            emotion.setEmotionType(requestDTO.getEmotionType());
        }
        emotionRepository.save(emotion);
    }

    @Transactional
    public void deleteEmotion(String userId, LocalDate date){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Emotion emotion = emotionRepository.findByUserAndDate(user,date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 등록된 감정이 없습니다."));

        emotionRepository.delete(emotion);
    }
}
