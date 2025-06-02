package com.team.teamreadioserver.user.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

// 회원 조건검색용 DTO
@Getter
@Setter
public class AdminUserSearchDTO {
  private int page = 1;           // 기본 1페이지
  private int size = 10;          // 기본 페이지당 10개
  private int offset;

  private String searchType;      // id or email
  private String searchValue;

  private String startDate;       // yyyy-MM-dd
  private String endDate;         // yyyy-MM-dd

  //  private String userTypes;       // "일반회원,관리자" 이런식으로 콤마 구분
  private String userTypesString;
  private List<String> userTypes;

  // userTypesString 값이 설정될 때 userTypes List를 자동으로 채우도록 추가
  public void setUserTypesString(String userTypesString) {
    this.userTypesString = userTypesString;
    if (userTypesString != null && !userTypesString.isEmpty()) {
      // 콤마로 분리하여 List<String>으로 변환
      this.userTypes = Arrays.asList(userTypesString.split(","));
    } else {
      this.userTypes = null; // 값이 없으면 null로 설정
    }
  }

  private String reportStatus;    // Y or N or null

  public void calculateOffset() {
    this.offset = (page - 1) * size;
  }
}
