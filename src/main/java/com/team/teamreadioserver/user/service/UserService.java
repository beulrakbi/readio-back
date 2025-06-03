package com.team.teamreadioserver.user.service;

import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.enums.PrivateStatus;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.UserEditRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.entity.User;
import com.team.teamreadioserver.user.entity.UserRole;
import com.team.teamreadioserver.user.mapper.UserMapper;
import com.team.teamreadioserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

//    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
//        this.userMapper = userMapper;
//        this.passwordEncoder = passwordEncoder;
//    }

    // 회원가입
    @Transactional
    public void joinUser(JoinRequestDTO joinRequestDTO) {
        // 비밀번호 암호화해서 DB에 저장
        String encodedPwd = passwordEncoder.encode(joinRequestDTO.getUserPwd());
        joinRequestDTO.setUserPwd(encodedPwd);
        userMapper.insertUser(joinRequestDTO);

        // 사용자 생성
        User user = User.builder()
                .userId(joinRequestDTO.getUserId())
                .userName(joinRequestDTO.getUserName())
                .userPwd(encodedPwd)
                .userEmail(joinRequestDTO.getUserEmail())
                .userPhone(joinRequestDTO.getUserPhone())
                .userBirthday(LocalDate.parse(joinRequestDTO.getUserBirthday()))
                .userRole(UserRole.USER)
                .userEnrollDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
        profileRepository.save(createDefaultProfile(user));
    }

    // 필명 자동 생성
    private Profile createDefaultProfile(User user) {
        int suffix = 1;
        String base = "Readio 기본 필명 ";
        while (profileRepository.existsByPenName(base + suffix)) {
            suffix++;
        }
        return Profile.builder()
                .user(user)
                .penName(base + suffix)
                .biography("")
                .isPrivate(PrivateStatus.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();
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

  public String findId(String userName, String userPhone) {
    return userMapper.findIdByNameAndPhone(userName, userPhone);
  }

  public boolean verifyUserForPwdReset(String userId, String userEmail) {
    return userMapper.findPwdByIdAndEmail(userId, userEmail) != null;
  }

  public void resetPassword(String userId, String newPassword) {
    String hashedPwd = passwordEncoder.encode(newPassword);
    userMapper.updatePassword(userId, hashedPwd);
  }

  // 회원 탈퇴 처리
  public boolean deleteUser(String userId) {
    System.out.println("userService.deleteUser 호출됨: userId=" + userId);
    int result = userMapper.deleteUserById(userId);
    System.out.println("삭제 결과: " + result);
    return result > 0;
//    return userMapper.deleteUserById(userId) > 0;
  }

  // 비밀번호 확인
  public boolean verifyPasswordForDelete(String userId, String inputPassword) {
    String storedHashedPassword = userMapper.getPasswordByUserId(userId);

    logger.info("사용자가 입력한 비번: " + inputPassword);
    logger.info("db에서 읽어온 비밀번호해시:" + storedHashedPassword);

    if(storedHashedPassword == null) return false;

    // 디버깅..

    return passwordEncoder.matches(inputPassword, storedHashedPassword);
  }



}