package com.team.teamreadioserver.user.service;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.LoginRequestDTO;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder){
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  // 아이디 중복 체크
  public boolean isUserIdAvailable(String userId) {
    return userMapper.countByUserId(userId) == 0;
  }

  // 이메일 중복 체크
  public boolean isEmailAvailable(String userEmail) {
    return userMapper.countByUserEmail(userEmail) == 0;
  }

  // 회원가입
  @Transactional
  public void joinUser(JoinRequestDTO joinRequestDTO) {
    String encodedPwd = passwordEncoder.encode(joinRequestDTO.getUserPwd());
    joinRequestDTO.setUserPwd(encodedPwd);
    userMapper.insertUser(joinRequestDTO);
  }

  @Transactional
  public User login(LoginRequestDTO loginRequestDTO) {

    User user = userMapper.findByUserId(loginRequestDTO.getUserId());
    if (user != null && passwordEncoder.matches(loginRequestDTO.getUserPwd(), user.getUserPwd())) {
      return user;
    }
    // 로그인 실패
    return null;
  }


}
