package com.team.teamreadioserver.user.mapper;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
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

}
