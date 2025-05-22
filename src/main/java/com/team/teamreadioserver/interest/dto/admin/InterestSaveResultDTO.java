package com.team.teamreadioserver.interest.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 서버가 처리한 결과 데이터 (저장 성공 항목 응답용)
@Getter
@AllArgsConstructor
public class InterestSaveResultDTO {
    private List<String> savedCategories;
    private List<String> savedKeywords;
}