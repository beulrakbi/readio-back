package com.team.teamreadioserver.clicklog.service;

import com.team.teamreadioserver.clicklog.dto.ClickLogDTO;
import com.team.teamreadioserver.clicklog.entity.ClickLog;
import com.team.teamreadioserver.clicklog.repository.ClickLogRepository;
import org.springframework.stereotype.Service;

@Service
public class ClickLogService {

    private ClickLogRepository clickLogRepository;

    public ClickLogService(ClickLogRepository clickLogRepository) {
        this.clickLogRepository = clickLogRepository;
    }

    public void saveClick(ClickLogDTO dto) {
        ClickLog log = ClickLog.builder()
                .userId(dto.getUserId())
                .contentType(dto.getContentType())
                .contentId(dto.getContentId())
                .build();
        clickLogRepository.save(log);
    }
}