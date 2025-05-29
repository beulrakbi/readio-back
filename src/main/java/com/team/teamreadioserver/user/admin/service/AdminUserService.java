package com.team.teamreadioserver.user.admin.service;

import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import com.team.teamreadioserver.user.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

  private final AdminMapper adminMapper;

  public List<AdminUserViewDTO> getAdminUserList() {
    return adminMapper.selectAdminUserList();
  }
}
