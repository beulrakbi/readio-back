package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.statistics.dto.UserBehaviorLogDTO;
import com.team.teamreadioserver.statistics.entity.UserBehaviorLog;
import com.team.teamreadioserver.statistics.repository.UserBehaviorLogRepository;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBehaviorLogService {

    private final UserRepository userRepository;
    private final UserBehaviorLogRepository userBehaviorLogRepository;

    public void saveUserBehaviorLog(UserBehaviorLogDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + dto.getUserId()));

        UserBehaviorLog log = UserBehaviorLog.builder()
                .user(user)
                .section(dto.getSection())
                .stayTime(dto.getStayTime())
                .clickCount(dto.getClickCount())
                .logDate(dto.getLogDate())
                .build();

        userBehaviorLogRepository.save(log);
    }
}
