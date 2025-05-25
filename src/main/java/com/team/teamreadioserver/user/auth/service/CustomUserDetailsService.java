package com.team.teamreadioserver.user.auth.service;//package com.team.teamreadioserver.user.auth.service;


import com.team.teamreadioserver.user.mapper.UserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.team.teamreadioserver.user.entity.UserRole.*;

//MyBatis 연동 사용자 조회

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserMapper userMapper;

  public CustomUserDetailsService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    com.team.teamreadioserver.user.entity.User user = userMapper.findByUserId(username);
    if(user == null) {
      throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username);
    }
     return org.springframework.security.core.userdetails.User.builder()
         .username(user.getUserId())
         .password(user.getUserPwd())
         .roles(user.getUserRole().name())
         .build();
  }

}

