package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.statistics.dto.ClickLogDTO;
import com.team.teamreadioserver.statistics.dto.PopularBookDTO;
import com.team.teamreadioserver.statistics.dto.PopularVideoDTO;
import com.team.teamreadioserver.statistics.entity.ClickLog;
import com.team.teamreadioserver.statistics.repository.ClickLogRepository;
import com.team.teamreadioserver.search.repository.BookRepository;
import com.team.teamreadioserver.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogRepository clickLogRepository;
    private final BookRepository bookRepository;
    private final VideoRepository videoRepository;

    // 클릭 로그 저장
    public void saveClick(ClickLogDTO dto) {
        System.out.println(">>> userId = " + dto.getUserId()); // null 체크
        ClickLog log = ClickLog.builder()
                .userId(dto.getUserId())
                .contentType(dto.getContentType())
                .contentId(dto.getContentId())
                .build();

        clickLogRepository.save(log);
    }
}
