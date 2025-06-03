package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.UserEditRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.mapper.UserMapper;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 회원가입(중복체크 포함),
@RestController
@RequestMapping("/users")
@Tag(name = "회원 API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "회원가입", description = "신규 사용자를 등록한다.")
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequestDTO joinRequestDTO) {

        try { // 회원가입 로직도 예외 처리 추가
            userService.joinUser(joinRequestDTO);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "회원가입-아이디 중복확인", description = "회원가입 시 아이디 중복확인을 진행한다.")
    @GetMapping("/join/check-id")
    public Map<String, Boolean> checkUserId(@RequestParam String userId) {
        boolean isAvailable = userService.isIdAvailable(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", !isAvailable);
        return response;
    }

    @Operation(summary = "회원가입-이메일 중복확인", description = "회원가입 시 이메일 중복확인을 진행한다.")
    @GetMapping("/join/check-email")
    public Map<String, Boolean> checkUserEmail(@RequestParam String userEmail) {
        boolean isAvailable = userService.isEmailAvailable(userEmail);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", !isAvailable);
        return response;
    }

    @Operation(summary = "회원가입-휴대폰번호 중복확인", description = "회원가입 시 이메일 중복확인을 진행한다.")
    @GetMapping("/join/check-phone")
    public Map<String, Boolean> checkUserPhone(@RequestParam String userPhone) {
        boolean isAvailable = userService.isPhoneAvailable(userPhone);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", !isAvailable);
        return response;
    }

    // 비밀번호 확인(내정보수정)
    @Operation(summary = "페이지 진입 전 비밀번호 확인", description = "현재 비밀번호 확인을 거친 후에 정보 수정이 가능하다.")
    @PostMapping("/verifypwd")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String inputPassword = request.get("password");

        boolean isValid = userService.verifyPassword(userId, inputPassword);
        if (isValid) {
            return ResponseEntity.ok("비밀번호 확인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다");
        }
    }

    // 회원정보 조회
    @Operation(summary = "내 정보 수정-회원정보조회", description = "회원정보 수정 시 해당 회원의 정보를 조회해온다.")
    @ResponseBody
    @GetMapping("/edit")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedUserId = userDetails.getUsername(); // JWT에서 추출된 사용자 ID
        logger.info("회원 정보 조회 요청: UserID = {}", authenticatedUserId); // 로그 추가

        UserInfoResponseDTO user = userService.getUserInfo(authenticatedUserId);
        if (user == null) {
            // 서비스에서 예외를 던지도록 수정했다면 이 부분은 필요 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
        return ResponseEntity.ok(user);
    }

    // 회원정보 수정
    @Operation(summary = "회원정보 수정", description = "로그인된 사용자는 회원정보 수정이 가능하다.")
    @PutMapping("/edit")
    public ResponseEntity<String> updateUser(@RequestBody UserEditRequestDTO userEditRequestDTO,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        String authenticatedUserId = userDetails.getUsername();

        if (!authenticatedUserId.equals(userEditRequestDTO.getUserId())) {
            logger.warn("회원정보 수정 시도: 인증된 사용자({}), 요청 userId({}) 불일치", authenticatedUserId, userEditRequestDTO.getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자신의 정보만 수정할 수 있습니다.");
        }

        try {

            int updatedCount = userService.updateUser(userEditRequestDTO);
            if (updatedCount == 1) {
                return ResponseEntity.ok("회원정보가 성공적으로 수정되었습니다."); // 200 OK
            } else {
                return ResponseEntity.badRequest().body("회원정보 수정에 실패했습니다: 대상이 없거나 변경사항이 없습니다."); // 400 Bad Request
            }
        } catch (Exception e) {
            // 실제 운영 환경에서는 예외 메시지를 그대로 노출하지 않고,
            // 로깅 후 일반적인 오류 메시지를 반환하기
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 회원정보 수정에 실패했습니다: " + e.getMessage()); // 500 Internal Server Error
        }
    }

  // 아이디 찾기(이름,전화번호로)
  @Operation(summary = "아이디찾기", description = "이름과 전화번호로 아이디찾기가 가능하다.")
  @GetMapping("account/findId")
  public ResponseEntity<?> findId(@RequestParam String name, @RequestParam String phone) {
    String id = userService.findId(name, phone);
    return id != null ? ResponseEntity.ok(id) : ResponseEntity.status(404).body("아이디 없음");
  }
  
  // 회원 탈퇴
  @Operation(summary = "회원탈퇴", description = "회원탈퇴가 가능하다.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable String userId) {
    boolean deleted = userService.deleteUser(userId);
    if (deleted) {
      return ResponseEntity.ok(Map.of("message", "탈퇴 완료"));
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "탈퇴 실패"));
    }
  }

  // 회원 탈퇴 전 비밀번호 확인
  @Operation(summary = "회원탈퇴 전 비밀번호 확인", description = "회원탈퇴 전 비밀번호 확인을 거친 후 탈퇴가 가능하다.")
  @PostMapping("/verifypwd/delete")
  public ResponseEntity<?> verifyPasswordForDelete(@RequestBody Map<String, String> request) {
    String userId = request.get("userId");
    String inputPassword = request.get("password");

    boolean isValid = userService.verifyPasswordForDelete(userId, inputPassword);
    if (isValid) {
      return ResponseEntity.ok("비밀번호 확인 성공");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다");
    }
  }



    @GetMapping("/test")
    public String test() {
        return "test";
    }

}