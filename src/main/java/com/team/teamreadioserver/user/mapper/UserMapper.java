package com.team.teamreadioserver.user.mapper;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
  int insertUser(JoinRequestDTO joinRequestDTO);

  int countByUserId(String userId);

  int countByUserEmail(String userEmail);
}
