package com.team.teamreadioserver.user.auth.service;//package com.team.teamreadioserver.user.auth.service;


import com.team.teamreadioserver.user.mapper.UserMapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// MyBatis 연동 사용자 조회

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserMapper userMapper;

  public CustomUserDetailsService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("로그인 요청 username: " + username);
    com.team.teamreadioserver.user.entity.User user = userMapper.findByUserId(username);
    if(user == null) {
      System.out.println("사용자 없음:" + username + " (DB조회 결과 null)");
      throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.:" + username);
    }
    System.out.println("사용자 찾기:" + user.getUserId());
    System.out.println("암호화된 비밀번호:" + user.getUserPwd());
    System.out.println("권한:" + user.getUserRole());

    String userRoleName = (user.getUserRole() != null) ? user.getUserRole().name() : "USER";
    if (user.getUserRole() == null) {
      System.err.println("경고: 사용자 " + user.getUserId() + "의 역할(userRole)이 DB에서 null로 로드되었습니다. 기본값 USER로 설정합니다.");
    }

     return org.springframework.security.core.userdetails.User.builder()
         .username(user.getUserId())
         .password(user.getUserPwd())
         .roles(userRoleName) // 내부적으로 ROLE_ 접두어 자동으로 붙임 예)"ROLE_ADMIN"
         .build();
  }

}

