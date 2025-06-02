package com.team.teamreadioserver.statistics.service;

import com.team.teamreadioserver.interest.repository.UserInterestCategoryRepository;
import com.team.teamreadioserver.interest.repository.UserInterestKeywordRepository;
import com.team.teamreadioserver.statistics.dto.InterestDiffDTO;
import com.team.teamreadioserver.statistics.dto.InterestTrendDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestTrendService {

    private final UserInterestKeywordRepository keywordRepository;
    private final UserInterestCategoryRepository categoryRepository;

    public List<InterestTrendDTO> getInterestTrend(
            String type,
            String granularity,
            LocalDate startDate,
            LocalDate endDate,
            String sort,
            Integer limit
    ) {
        String format = switch (granularity.toLowerCase()) {
            case "daily" -> "%Y-%m-%d";
            case "weekly" -> "%Y-%u";
            case "monthly" -> "%Y-%m";
            default -> throw new IllegalArgumentException("Invalid granularity");
        };

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> raw;
        if (type.equalsIgnoreCase("keyword")) {
            raw = keywordRepository.findKeywordTrend(start, end, format);
        } else if (type.equalsIgnoreCase("category")) {
            raw = categoryRepository.findCategoryTrend(start, end, format);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }

        List<InterestTrendDTO> dtos = raw.stream()
                .map(row -> InterestTrendDTO.builder()
                        .period((String) row[0])
                        .label((String) row[1])
                        .count(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());

        // 정렬
        Comparator<InterestTrendDTO> comparator = switch (sort) {
            case "count" -> Comparator.comparing(InterestTrendDTO::getCount).reversed();
            case "label" -> Comparator.comparing(InterestTrendDTO::getLabel);
            case "date" -> Comparator.comparing(InterestTrendDTO::getPeriod);
            default -> Comparator.comparing(InterestTrendDTO::getCount).reversed();
        };
        dtos = dtos.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        //  limit 적용
        if (limit != null && limit > 0 && limit < dtos.size()) {
            return dtos.subList(0, limit);
        }

        return dtos;
    }


    //사용자 관심사 추세 비교
    public List<InterestDiffDTO> getInterestDiff(
            String type,
            String month1,
            String month2,
            String sort,
            Integer limit
    ) {
        // 월의 시작/끝 구하기
        LocalDate date1 = LocalDate.parse(month1 + "-01");
        LocalDateTime start1 = date1.atStartOfDay();
        LocalDateTime end1 = date1.withDayOfMonth(date1.lengthOfMonth()).atTime(LocalTime.MAX);

        LocalDate date2 = LocalDate.parse(month2 + "-01");
        LocalDateTime start2 = date2.atStartOfDay();
        LocalDateTime end2 = date2.withDayOfMonth(date2.lengthOfMonth()).atTime(LocalTime.MAX);

        List<Object[]> raw1, raw2;

        if (type.equalsIgnoreCase("keyword")) {
            raw1 = keywordRepository.findKeywordTrend(start1, end1, "%Y-%m");
            raw2 = keywordRepository.findKeywordTrend(start2, end2, "%Y-%m");
        } else if (type.equalsIgnoreCase("category")) {
            raw1 = categoryRepository.findCategoryTrend(start1, end1, "%Y-%m");
            raw2 = categoryRepository.findCategoryTrend(start2, end2, "%Y-%m");
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Map<label, count>
        Map<String, Long> map1 = raw1.stream().collect(Collectors.toMap(row -> (String) row[1], row -> ((Number) row[2]).longValue()));
        Map<String, Long> map2 = raw2.stream().collect(Collectors.toMap(row -> (String) row[1], row -> ((Number) row[2]).longValue()));

        Set<String> allLabels = new HashSet<>();
        allLabels.addAll(map1.keySet());
        allLabels.addAll(map2.keySet());

        List<InterestDiffDTO> result = allLabels.stream()
                .map(label -> {
                    long count1 = map1.getOrDefault(label, 0L);
                    long count2 = map2.getOrDefault(label, 0L);
                    return InterestDiffDTO.builder()
                            .label(label)
                            .countMonth1(count1)
                            .countMonth2(count2)
                            .diff(count2 - count1)
                            .build();
                })
                .collect(Collectors.toList());

        // 정렬
        Comparator<InterestDiffDTO> comparator = switch (sort) {
            case "month1" -> Comparator.comparing(InterestDiffDTO::getCountMonth1).reversed();
            case "month2" -> Comparator.comparing(InterestDiffDTO::getCountMonth2).reversed();
            case "label" -> Comparator.comparing(InterestDiffDTO::getLabel);
            default -> Comparator.comparing(InterestDiffDTO::getDiff).reversed(); // 기본: diff
        };
        result = result.stream().sorted(comparator).collect(Collectors.toList());

        if (limit != null && limit > 0 && limit < result.size()) {
            return result.subList(0, limit);
        }
        return result;
    }

}
