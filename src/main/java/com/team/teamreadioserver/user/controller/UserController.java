package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.dto.UserEditRequestDTO;
import com.team.teamreadioserver.user.dto.UserInfoResponseDTO;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "회원 API", description = "회원가입 관련 API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        response.put("exist" , !isAvailable);
        return response;
    }

    @Operation(summary = "회원가입-이메일 중복확인", description = "회원가입 시 이메일 중복확인을 진행한다.")
    @GetMapping("/join/check-email")
    public Map<String, Boolean> checkUserEmail(@RequestParam String userEmail) {
        boolean isAvailable = userService.isEmailAvailable(userEmail);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist" , !isAvailable);
        return response;
    }

    @Operation(summary = "회원가입-휴대폰번호 중복확인", description = "회원가입 시 이메일 중복확인을 진행한다.")
    @GetMapping("/join/check-phone")
    public Map<String, Boolean> checkUserPhone(@RequestParam String userPhone) {
        boolean isAvailable = userService.isPhoneAvailable(userPhone);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist" , !isAvailable);
        return response;
    }

    // 비밀번호 확인
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
    @Operation(summary = "회원정보조회", description = "회원정보 수정 시 정보를 조회해온다.")
    @GetMapping("/edit")
    public UserInfoResponseDTO getUserInfo(@RequestParam String userId) {
        return userService.getUserInfo(userId);
    }

    // 회원정보 수정
    @Operation(summary = "회원정보 수정", description = "회원정보 수정이 가능하다.")
    @PutMapping("/edit")
    public String updateUser(@RequestBody UserEditRequestDTO userEditRequestDTO) {
        int updatedCount = userService.updateUser(userEditRequestDTO);
        if (updatedCount == 1) {
            return "success";
        }
        return "fail";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }


}