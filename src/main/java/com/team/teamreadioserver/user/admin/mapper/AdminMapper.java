package com.team.teamreadioserver.user.admin.mapper;

import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
  
  // 관리자-회원목록 조회
  List<AdminUserViewDTO> selectAdminUserList();
}
