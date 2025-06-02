package com.team.teamreadioserver.user.admin.mapper;

import com.team.teamreadioserver.user.admin.dto.AdminUserSearchDTO;
import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
  
  // 관리자-회원목록 조회(삭제)
  //List<AdminUserViewDTO> selectAdminUserList();

  // 검색/페이징 포함된 목록 조회 추가
  List<AdminUserViewDTO> selectAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  // 관리자-회원검색
  int countAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  // 관리자-회원권한변경
  int updateUserRole(@Param("userId") String userId, @Param("newRole") String newRole);

  // 관리자-회원삭제
  void deleteUser(@Param("userId") String userId);



}
