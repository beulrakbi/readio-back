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
        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        boolean isTotal = "none".equalsIgnoreCase(granularity);
        String format = switch (granularity.toLowerCase()) {
            case "daily" -> "%Y-%m-%d";
            case "weekly" -> "%Y-%u";
            case "monthly" -> "%Y-%m";
            case "none" -> null;
            default -> throw new IllegalArgumentException("Invalid granularity");
        };

        List<Object[]> raw;

        if (type.equalsIgnoreCase("keyword")) {
            raw = isTotal
                    ? keywordRepository.findKeywordTrendTotal(start, end)
                    : keywordRepository.findKeywordTrend(start, end, format);
        } else if (type.equalsIgnoreCase("category")) {
            raw = isTotal
                    ? categoryRepository.findCategoryTrendTotal(start, end)
                    : categoryRepository.findCategoryTrend(start, end, format);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }

        List<InterestTrendDTO> dtos = raw.stream()
                .map(row -> InterestTrendDTO.builder()
                        .period(isTotal ? "" : (String) row[0])
                        .label((String) row[isTotal ? 0 : 1])
                        .count(((Number) row[isTotal ? 1 : 2]).longValue())
                        .build())
                .collect(Collectors.toList());

        Map<String, Map<String, Long>> merged = new HashMap<>();
        for (InterestTrendDTO dto : dtos) {
            merged
                    .computeIfAbsent(dto.getPeriod(), k -> new HashMap<>())
                    .merge(dto.getLabel(), dto.getCount(), Long::sum);
        }

        dtos = merged.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream()
                        .map(inner -> new InterestTrendDTO(e.getKey(), inner.getKey(), inner.getValue())))
                .collect(Collectors.toList());

        Comparator<InterestTrendDTO> comparator = switch (sort) {
            case "label" -> Comparator.comparing(InterestTrendDTO::getLabel);
            case "date" -> Comparator.comparing(InterestTrendDTO::getPeriod);
            case "count" -> Comparator.comparing(InterestTrendDTO::getCount).reversed();
            default -> Comparator.comparing(InterestTrendDTO::getCount).reversed();
        };
        dtos = dtos.stream().sorted(comparator).collect(Collectors.toList());

        if (limit != null && limit > 0 && limit < dtos.size()) {
            return dtos.subList(0, limit);
        }

        return dtos;
    }

    // 리팩토링된 getInterestDiff()
    public List<InterestDiffDTO> getInterestDiff(
            String type,
            String month1,
            String month2,
            String sort,
            Integer limit
    ) {
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
        Map<String, Long> map1 = raw1.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[1],
                        Collectors.summingLong(row -> ((Number) row[2]).longValue())
                ));
        Map<String, Long> map2 = raw2.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[1],
                        Collectors.summingLong(row -> ((Number) row[2]).longValue())
                ));

        // 각 월의 Top N label 뽑기
        List<String> topMonth1Labels = map1.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .map(Map.Entry::getKey)
                .toList();

        List<String> topMonth2Labels = map2.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .map(Map.Entry::getKey)
                .toList();

        // 두 월의 label 합집합
        Set<String> topLabels = new HashSet<>();
        topLabels.addAll(topMonth1Labels);
        topLabels.addAll(topMonth2Labels);

        // ⬇합집합 기준 DTO 생성
        List<InterestDiffDTO> result = topLabels.stream()
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

        // 정렬 기준 적용 (정렬은 전체에 대해 수행)
        Comparator<InterestDiffDTO> comparator = switch (sort) {
            case "month1" -> Comparator.comparing(InterestDiffDTO::getCountMonth1).reversed();
            case "month2" -> Comparator.comparing(InterestDiffDTO::getCountMonth2).reversed();
            case "label" -> Comparator.comparing(InterestDiffDTO::getLabel);
            default -> Comparator.comparing(InterestDiffDTO::getDiff).reversed();
        };

        return result.stream().sorted(comparator).toList();
    }
}
