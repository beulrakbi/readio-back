package com.team.teamreadioserver.user.admin.mapper;

import com.team.teamreadioserver.user.admin.dto.AdminUserSearchDTO;
import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

  // 관리자-회원목록조회(페이징 포함)
  List<AdminUserViewDTO> selectAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  // 관리자-조건검색
  int countAdminUserList(@Param("search") AdminUserSearchDTO searchDTO);

  // 관리자-회원권한변경
  int updateUserRole(@Param("userId") String userId, @Param("newRole") String newRole);

  // 관리자-회원삭제
  void deleteUser(@Param("userId") String userId);

  // 관리자-신규가입 회원 수(당월 기준)
  int countUsersThisMonth();

  // 관리자-전체 회원 수
  int countAllUser();

}
