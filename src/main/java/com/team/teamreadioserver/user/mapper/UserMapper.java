package com.team.teamreadioserver.user.mapper;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.UserEditRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
  int insertUser(JoinRequestDTO joinRequestDTO);

  int countByUserId(String userId);

  int countByUserEmail(String userEmail);

  int countByUserPhone(String userPhone);

  User findByUserId(@Param("userId") String userId);

  // UserId 로 비밀번호 조회(검증)
  String getPasswordByUserId(@Param("userId") String userId);

  UserInfoResponseDTO selectUserById(@Param("userId") String userId);

  int updateUser(UserEditRequestDTO userEditRequestDTO);

  String selectPasswordByUserId(@Param("userId") String userId);

}
