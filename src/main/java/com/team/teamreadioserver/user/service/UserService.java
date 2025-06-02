package com.team.teamreadioserver.user.service;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.UserEditRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @Transactional
    public void joinUser(JoinRequestDTO joinRequestDTO) {
        // 비밀번호 암호화해서 DB에 저장
        String encodedPwd = passwordEncoder.encode(joinRequestDTO.getUserPwd());
        joinRequestDTO.setUserPwd(encodedPwd);
        userMapper.insertUser(joinRequestDTO);
    }

    // 아이디 중복 체크
    public boolean isIdAvailable(String userId) {
        return userMapper.countByUserId(userId) == 0;
    }

    // 이메일 중복 체크
    public boolean isEmailAvailable(String userEmail) {
        return userMapper.countByUserEmail(userEmail) == 0;
    }

    // 전화번호 중복 체크
    public boolean isPhoneAvailable(String userPhone) {
        return userMapper.countByUserPhone(userPhone) == 0;
    }

    // 로그인 시 아이디 조회하기
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    // 비밀번호 확인
    public boolean verifyPassword(String userId, String inputPassword) {
        String storedHashedPassword = userMapper.getPasswordByUserId(userId);

        logger.info("사용자가 입력한 비번: " + inputPassword);
        logger.info("db에서 읽어온 비밀번호해시:" + storedHashedPassword);

        if(storedHashedPassword == null) return false;

        // 디버깅..

        return passwordEncoder.matches(inputPassword, storedHashedPassword);
    }

    // 회원정보조회
    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo(String userId) {
        logger.info("getUserInfo: 사용자 ID 조회 요청 - {}", userId);
        UserInfoResponseDTO userInfo = userMapper.selectUserById(userId);
        if(userInfo == null) {
            throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다." + userId);
        }
        logger.info("getUserInfo: 사용자 정보 조회 성공 - {}", userInfo.getUserId());
        return userInfo;
    }

    // 회원정보수정
    @Transactional
    public int updateUser(UserEditRequestDTO userEditRequestDTO) {
        // 비밀번호 입력했으면 암호화해서 저장
        if (userEditRequestDTO.getUserPwd() != null && !userEditRequestDTO.getUserPwd().isEmpty()) {
            userEditRequestDTO.setUserPwd(passwordEncoder.encode(userEditRequestDTO.getUserPwd()));
        }

        // 수정용 DTO로 전체 수정 가능
        return userMapper.updateUser(userEditRequestDTO);
    }

}