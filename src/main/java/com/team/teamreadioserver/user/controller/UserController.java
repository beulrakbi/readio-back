package com.team.teamreadioserver.user.controller;

import com.team.teamreadioserver.user.dto.JoinRequestDTO;
import com.team.teamreadioserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
//        if (!userService.isIdAvailable(joinRequestDTO.getUserId())) {
//            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
//        }
//        if (!userService.isEmailAvailable(joinRequestDTO.getUserEmail())) {
//            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
//        }

        userService.joinUser(joinRequestDTO);
        return ResponseEntity.ok("회원가입 성공");
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


    @GetMapping("/test")
    public String test() {
        return "test";
    }


}
