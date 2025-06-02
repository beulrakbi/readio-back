package com.team.teamreadioserver.user.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminUserListResponse {
  private List<AdminUserViewDTO> users;
  private int totalCount;
  private int totalPages;
}
