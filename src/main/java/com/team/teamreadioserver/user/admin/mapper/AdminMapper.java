package com.team.teamreadioserver.user.admin.mapper;

import com.team.teamreadioserver.user.admin.dto.AdminUserSearchDTO;
import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
  
  // 관리자-회원목록 조회
  //List<AdminUserViewDTO> selectAdminUserList();

  // 기존 단순 목록 조회는 삭제하고
  // 검색/페이징 포함된 목록 조회 추가
  List<AdminUserViewDTO> selectAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  int countAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  void updateUserRole(@Param("userId") String userId, @Param("newRole") String newRole);

  void deleteUser(@Param("userId") String userId);



}
